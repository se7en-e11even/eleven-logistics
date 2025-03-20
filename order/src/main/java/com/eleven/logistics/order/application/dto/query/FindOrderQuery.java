package com.eleven.logistics.order.application.dto.query;

import com.eleven.logistics.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record FindOrderQuery(
        UUID orderId,
        UUID supplyId,
        UUID receiverId,
        UUID deliveryId,
        String orderStatus,
        String request,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FindOrderProductQuery> orderProductDtoList
) {
    public static FindOrderQuery of(Order order) {
        List<FindOrderProductQuery> orderProductList = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    return FindOrderProductQuery.create(
                            orderProduct.getOrderProductId(),
                            orderProduct.getProductId(),
                            orderProduct.getPrice(),
                            orderProduct.getQuantity()
                    );
                })
                .toList();

        return FindOrderQuery.builder()
                .orderId(order.getOrderId())
                .supplyId(order.getSupplyId())
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDeliveryId())
                .orderStatus(order.getOrderStatus().name())
                .request(order.getRequest())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderProductDtoList(orderProductList)
                .build();
    }
}
