package com.eleven.logistics.order.domain.repository;

import com.eleven.logistics.order.domain.entity.Order;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    // Order 가 애그리거트 루트 이므로 연관관계 혹은 조인을 통해 OrderProduct 도 함께 수정되도록 해야 한다.

    Order save(Order order);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderProductList WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdAndDeletedAtIsNull(UUID orderId);
}
