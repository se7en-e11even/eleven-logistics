package com.eleven.logistics.order.domain.repository;

import com.eleven.logistics.order.domain.entity.Order;

import java.util.UUID;

public interface OrderRepository {
    // Order 가 애그리거트 루트 이므로 연관관계 혹은 조인을 통해 OrderProduct 도 함께 수정되도록 해야 한다.

    Order save(Order order);

    Order findByOrderId(UUID orderId);

    // queryDSL 사용으로 변경함.
//    @Query("SELECT o FROM Order o JOIN FETCH o.orderProductList WHERE o.orderId = :orderId")
//    Optional<Order> findByOrderIdAndDeletedAtIsNull(UUID orderId);
}
