package com.eleven.logistics.order.infrastructure.repository;

import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.repository.OrderRepositoryCustom;
import com.eleven.logistics.order.domain.vo.FindOrder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.eleven.logistics.order.domain.entity.QOrder.order;
import static com.eleven.logistics.order.domain.entity.QOrderProduct.orderProduct;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 주문 상세 조회
     */
    public Optional<Order> findById(UUID orderId) {
        Order fetchedOrder = jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.orderProductList, orderProduct).fetchJoin()
                .where(order.orderId.eq(orderId)
                        .and(order.deletedAt.isNull()))
                .fetchOne();
        return Optional.ofNullable(fetchedOrder);
    }

    /**
     * 주문 검색
     * 목록, 페이징, 키워드 검색
     */
    public Page<FindOrder> retrieve(String keyword, Pageable pageable) {
        List<FindOrder> content = getOrderList(keyword, pageable);
        return new PageImpl<>(content, pageable, content.size());
    }

    /**
     * 페이징 조회 메서드
     */
    private List<FindOrder> getOrderList(String keyword, Pageable pageable) {
        List<Tuple> results = jpaQueryFactory
                .select(order.orderId, order.supplyId, order.receiverId, order.deliveryId,
                        order.orderStatus.stringValue(), order.request, order.createdAt, order.updatedAt,
                        orderProduct.orderProductId, orderProduct.productId, orderProduct.price, orderProduct.quantity)
                .from(order)
                .leftJoin(orderProduct)
                .on(order.orderId.eq(orderProduct.order.orderId)
                        .and(orderProduct.deletedAt.isNull()))
                .where(getWhereConditions(keyword))
                .orderBy(getAllOrderSpecifiers(pageable).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 결과를 직접 매핑하여 List<ResponseDto> 변환
        Map<UUID, FindOrder> queryMap = new LinkedHashMap<>();

        for (Tuple tuple : results) {
            UUID orderId = tuple.get(order.orderId);
            FindOrder query = queryMap.computeIfAbsent(orderId, id -> new FindOrder(
                    id,
                    tuple.get(order.supplyId),
                    tuple.get(order.receiverId),
                    tuple.get(order.deliveryId),
                    tuple.get(order.orderStatus.stringValue()),
                    tuple.get(order.request),
                    tuple.get(order.createdAt),
                    tuple.get(order.updatedAt),
                    new ArrayList<>()
            ));

            if (tuple.get(orderProduct.orderProductId) != null) {
                FindOrder.FindOrderProduct orderProductQuery = new FindOrder.FindOrderProduct(
                        tuple.get(orderProduct.orderProductId),
                        tuple.get(orderProduct.productId),
                        tuple.get(orderProduct.price),
                        tuple.get(orderProduct.quantity)
                );
                query.orderProductList().add(orderProductQuery);
            }
        }

        return new ArrayList<>(queryMap.values());
    }

    /**
     * where conditions 조회 조건: deletedAt
     */
    private BooleanBuilder getWhereConditions(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        // Order 의 deletedAt
        builder.and(order.deletedAt.isNull());

        // OrderProduct 의 deletedAt left join 을 고려하여야 한다.
//        builder.and(orderProduct.deletedAt.isNull().or(orderProduct.orderProductId.isNull()));

        // keyword 가 있을 시 검색 조건 추가
        if (StringUtils.hasText(keyword)) {
            builder.and(order.request.containsIgnoreCase(keyword));
            // keyword 적용할 필드가 있으면 조건 추가
        }

        return builder;
    }

    /**
     * 정렬 조건
     */
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            com.querydsl.core.types.Order direction = sortOrder.isAscending() ?
                    com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
            switch (sortOrder.getProperty()) {
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, order.createdAt));
                    break;
                case "updatedAt":
                    orders.add(new OrderSpecifier<>(direction, order.updatedAt));
                    break;
                default:
                    break;
            }
        }
        return orders;
    }
}
