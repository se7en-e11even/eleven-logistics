package com.eleven.logistics.product.infrastructure;

import com.eleven.logistics.product.application.dto.ResponseDto;
import com.eleven.logistics.product.common.exception.CustomException;
import com.eleven.logistics.product.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.product.common.resolver.dto.PageResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.eleven.logistics.product.domain.entity.QProduct.product;
import static com.eleven.logistics.product.domain.exception.ProductErrorCode.NO_KEYWORD;
import static com.eleven.logistics.product.domain.exception.ProductErrorCode.ORDER_BY_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 상품 목록 조회
     * page, order
     */
    public PageResponseDto<ResponseDto> readProducts(PageRequestDto dto) {
        List<ResponseDto> content = getProductList(dto);
        long total = getTotalCount();

        return new PageResponseDto<>(content, total);
    }

    /**
     * 상품 검색
     */
    public PageResponseDto<ResponseDto> retrieveProducts(String keyword, PageRequestDto dto) {
        List<ResponseDto> content = getProductList(keyword, dto);
        long total = getTotalCount();
        return new PageResponseDto<>(content, total);
    }

    /**
     * 페이징 조회 메서드
     */
    private List<ResponseDto> getProductList(PageRequestDto dto) {
        return jpaQueryFactory
                .select(Projections.constructor(ResponseDto.class,
                        product.productId,
                        product.companyId,
                        product.hubId,
                        product.name,
                        product.price,
                        product.quantity,
                        product.createdAt,
                        product.updatedAt
                        ))
                .from(product)
                .where(getWhereConditions())
                .offset(dto.getFirstIndex())
                .limit(dto.size())
                .orderBy(getOrderConditions(dto))
                .fetch();
    }

    /**
     * 검색 메서드
     */
    private List<ResponseDto> getProductList(String keyword, PageRequestDto dto) {
        return jpaQueryFactory
                .select(Projections.constructor(ResponseDto.class,
                        product.productId,
                        product.companyId,
                        product.hubId,
                        product.name,
                        product.price,
                        product.quantity,
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
    private long getTotalCount() {
        return Optional.ofNullable(jpaQueryFactory
                        .select(product.count())
                        .from(product)
                        .where(getWhereConditions())
                        .fetchOne()
                )
                .orElse(0L);
    }

    /**
     * 조회 조건: deletedAt
     */
    private BooleanBuilder getWhereConditions() {
        BooleanBuilder builder = new BooleanBuilder();

        // soft delete 정책을 사용한다.
        return builder.and(product.deletedAt.isNull());
    }

    /**
     * 조회 조건: deletedAt, keyword
     */
    private BooleanBuilder getWhereConditions(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new CustomException(NO_KEYWORD);
        }

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.deletedAt.isNull());

        BooleanExpression keywordCondition = product.name.containsIgnoreCase(keyword);
        builder.and(keywordCondition);

        return builder;
    }

    /**
     * 정렬 조건
     */
    private OrderSpecifier<?> getOrderConditions(PageRequestDto dto) {
        String order = dto.orderBy();

        return switch (order) {
            case "desc" -> product.createdAt.desc();
            case "asc" -> product.createdAt.asc();
            default -> throw new CustomException(ORDER_BY_NOT_FOUND);
        };
    }
}
