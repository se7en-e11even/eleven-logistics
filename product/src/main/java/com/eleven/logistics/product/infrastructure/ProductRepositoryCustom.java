package com.eleven.logistics.product.infrastructure;

import com.eleven.logistics.product.application.dto.ResponseDto;
import com.eleven.logistics.product.common.exception.CustomException;
import com.eleven.logistics.product.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.product.common.resolver.dto.PageResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.eleven.logistics.product.domain.entity.QProduct.product;
import static com.eleven.logistics.product.domain.exception.ProductErrorCode.ORDER_BY_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 상품 목록 조회
     * 페이징, 정렬
     */
    public PageResponseDto<ResponseDto> readProducts(PageRequestDto dto) {
        List<ResponseDto> content = getProductList(null, dto);
        long total = getTotalCount(null);

        return new PageResponseDto<>(content, total);
    }

    /**
     * 상품 검색
     */
    public PageResponseDto<ResponseDto> retrieveProducts(String keyword, PageRequestDto dto) {
        List<ResponseDto> content = getProductList(keyword, dto);
        long total = getTotalCount(keyword);
        return new PageResponseDto<>(content, total);
    }

    /**
     * 페이징 + 검색 메서드
     */
    private List<ResponseDto> getProductList(String keyword, PageRequestDto dto) {
        return jpaQueryFactory
                .select(Projections.constructor(ResponseDto.class,
                        product.productId,
                        product.companyId,
                        product.hubId,
                        product.name,
                        product.price,
                        product.stockQuantity,
                        product.createdAt,
                        product.updatedAt
                ))
                .from(product)
                .where(getWhereConditions(keyword))
                .offset(dto.getFirstIndex())
                .limit(dto.size())
                .orderBy(getOrderConditions(dto))
                .fetch();
    }

    /**
     * 전체 데이터 수 조회
     */
    private long getTotalCount(String keyword) {
        return Optional.ofNullable(jpaQueryFactory
                        .select(product.count())
                        .from(product)
                        .where(getWhereConditions(keyword))
                        .fetchOne()
                )
                .orElse(0L);
    }

    /**
     * 조회 조건
     */
    private BooleanBuilder getWhereConditions(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        // soft delete 정책 적용
        builder.and(product.deletedAt.isNull());

        // keyword 가 있을 경우 검색 조건 추가
        if (StringUtils.hasText(keyword)) {
            builder.and(product.name.containsIgnoreCase(keyword));
        }

        return builder;
    }

    /**
     * 정렬 조건
     */
    private OrderSpecifier<?> getOrderConditions(PageRequestDto dto) {
        String orderBy = dto.orderBy().toLowerCase();

        if (!StringUtils.hasText(orderBy)) {
            return product.createdAt.desc();
        }

        return switch (orderBy) {
            case "desc" -> product.createdAt.desc();
            case "asc" -> product.createdAt.asc();
            default -> throw new CustomException(ORDER_BY_NOT_FOUND);
        };
    }
}
