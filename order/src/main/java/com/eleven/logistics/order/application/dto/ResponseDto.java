package com.eleven.logistics.order.application.dto;

import com.eleven.logistics.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record ResponseDto(
        UUID orderId,
        UUID supplyId,
        UUID receiverId,
        UUID deliveryId,
        String orderStatus,
        String request,
        List<OrderProductDto> orderProductDtoList
) {
    public static ResponseDto of(Order order) {
        List<OrderProductDto> orderProductList = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    return OrderProductDto.create(
                            orderProduct.getOrderProductId(),
                            orderProduct.getProductId(),
                            orderProduct.getPrice(),
                            orderProduct.getQuantity()
                    );
                })
                .toList();

        return ResponseDto.builder()
                .orderId(order.getOrderId())
                .supplyId(order.getSupplyId())
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDeliveryId())
                .orderStatus(order.getOrderStatus().name())
                .request(order.getRequest())
                .orderProductDtoList(orderProductList)
                .build();
    }
}
