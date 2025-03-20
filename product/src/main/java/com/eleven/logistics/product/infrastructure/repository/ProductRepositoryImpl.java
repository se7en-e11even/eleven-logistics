package com.eleven.logistics.product.infrastructure.repository;

import com.eleven.logistics.product.application.dto.command.ListProductCommand;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.application.dto.query.ListProductQuery;
import com.eleven.logistics.product.common.exception.CustomException;
import com.eleven.logistics.product.domain.repository.ProductRepositoryCustom;
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
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 상품 검색
     * 상품 전체 목록, 페이징, 상품 keyword 검색
     */
    public ListProductQuery<FindProductQuery> retrieve(String keyword, ListProductCommand command) {
        List<FindProductQuery> content = getProductList(keyword, command);
        long total = getTotalCount(keyword);
        return new ListProductQuery<>(content, total);
    }

    /**
     * 페이징 + 검색 메서드
     */
    private List<FindProductQuery> getProductList(String keyword, ListProductCommand command) {
        return jpaQueryFactory
                .select(Projections.constructor(FindProductQuery.class,
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
                .offset(command.getFirstIndex())
                .limit(command.size())
                .orderBy(getOrderConditions(command))
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
    private OrderSpecifier<?> getOrderConditions(ListProductCommand command) {
        String orderBy = command.orderBy().toLowerCase();

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
