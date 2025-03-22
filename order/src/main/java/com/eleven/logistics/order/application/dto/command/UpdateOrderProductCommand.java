package com.eleven.logistics.order.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateOrderProductCommand(
        UUID orderProductId,
        UUID productId,
        Integer price,
        Integer quantity
) {
    public static UpdateOrderProductCommand of(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
        return UpdateOrderProductCommand.builder()
                .orderProductId(orderProductId)
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
