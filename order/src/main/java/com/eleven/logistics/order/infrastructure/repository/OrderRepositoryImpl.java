package com.eleven.logistics.order.infrastructure.repository;

import com.eleven.logistics.order.application.dto.command.ListOrderCommand;
import com.eleven.logistics.order.application.dto.query.FindOrderProductQuery;
import com.eleven.logistics.order.application.dto.query.FindOrderQuery;
import com.eleven.logistics.order.application.dto.query.ListOrderQuery;
import com.eleven.logistics.order.common.exception.CustomException;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.repository.OrderRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.eleven.logistics.order.domain.entity.QOrder.order;
import static com.eleven.logistics.order.domain.entity.QOrderProduct.orderProduct;
import static com.eleven.logistics.order.domain.exception.OrderErrorCode.ORDER_BY_NOT_FOUND;

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
    public ListOrderQuery<FindOrderQuery> retrieve(String keyword, ListOrderCommand command) {
        List<FindOrderQuery> content = getOrderList(keyword, command);
        long total = getTotalCount(keyword);
        return new ListOrderQuery<>(content, total);
    }

    /**
     * 페이징 조회 메서드
     */
    private List<FindOrderQuery> getOrderList(String keyword, ListOrderCommand command) {
        List<Tuple> results = jpaQueryFactory
                .select(order.orderId, order.supplyId, order.receiverId, order.deliveryId,
                        order.orderStatus.stringValue(), order.request, order.createdAt, order.updatedAt,
                        orderProduct.orderProductId, orderProduct.productId, orderProduct.price, orderProduct.quantity)
                .from(order)
                .leftJoin(orderProduct)
                .on(order.orderId.eq(orderProduct.order.orderId)
                        .and(orderProduct.deletedAt.isNull()))
                .where(getWhereConditions(keyword))
                .offset(command.getFirstIndex())
                .limit(command.size())
                .orderBy(getOrderConditions(command))
                .fetch();

        // 결과를 직접 매핑하여 List<ResponseDto> 변환
        Map<UUID, FindOrderQuery> queryMap = new LinkedHashMap<>();

        for (Tuple tuple : results) {
            UUID orderId = tuple.get(order.orderId);
            FindOrderQuery query = queryMap.computeIfAbsent(orderId, id -> new FindOrderQuery(
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
                FindOrderProductQuery orderProductQuery = new FindOrderProductQuery(
                        tuple.get(orderProduct.orderProductId),
                        tuple.get(orderProduct.productId),
                        tuple.get(orderProduct.price),
                        tuple.get(orderProduct.quantity)
                );
                query.orderProductDtoList().add(orderProductQuery);
            }
        }

        return new ArrayList<>(queryMap.values());
    }

    /**
     * 전체 데이터 수 조회
     */
    private long getTotalCount(String keyword) {
        return Optional.ofNullable(jpaQueryFactory
                        .select(order.count())
                        .from(order)
                        .where(getWhereConditions(keyword))
                        .fetchOne()
                )
                .orElse(0L);
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
    private OrderSpecifier<?> getOrderConditions(ListOrderCommand command) {
        String orderBy = command.orderBy().toLowerCase();

        if (!StringUtils.hasText(orderBy)) {
            return order.createdAt.desc();
        }

        return switch (orderBy) {
            case "desc" -> order.createdAt.desc();
            case "asc" -> order.createdAt.asc();
            default -> throw new CustomException(ORDER_BY_NOT_FOUND);
        };
    }
}
