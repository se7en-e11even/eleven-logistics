package com.eleven.logistics.product.infrastructure.repository;

import com.eleven.logistics.product.domain.repository.ProductRepositoryCustom;
import com.eleven.logistics.product.domain.vo.FindProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.eleven.logistics.product.domain.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 상품 검색
     * 상품 전체 목록, 페이징, 상품 keyword 검색
     */
    @Override
    public Page<FindProduct> retrieve(String keyword, Pageable pageable) {
        List<FindProduct> content = getProductList(keyword, pageable);
        return new PageImpl<>(content, pageable, content.size());
    }

    /**
     * 페이징 + 검색 메서드
     */
    private List<FindProduct> getProductList(String keyword, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(FindProduct.class,
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
                .orderBy(getAllOrderSpecifiers(pageable).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
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
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
            switch (sortOrder.getProperty()) {
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, product.createdAt));
                    break;
                case "updatedAt":
                    orders.add(new OrderSpecifier<>(direction, product.updatedAt));
                    break;
                default:
                    break;
            }
        }
        return orders;
    }
}
