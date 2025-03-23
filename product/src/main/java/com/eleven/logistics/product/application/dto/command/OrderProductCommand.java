package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderProductCommand(
        List<OrderProduct> productList
) {

    public static OrderProductCommand of(
            List<OrderProduct> productList
    ) {
        return OrderProductCommand.builder()
                .productList(productList)
                .build();
    }

    @Builder
    public record OrderProduct(
            UUID productId,
            Integer quantity
    ) {
        public static OrderProduct of(
                UUID productId,
                Integer quantity
        ) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
        }
    }
}
