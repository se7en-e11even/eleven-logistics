package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderProductCommand(
        List<Product> productList
) {

    public static OrderProductCommand of(
            List<Product> productList
    ) {
        return OrderProductCommand.builder()
                .productList(productList)
                .build();
    }

    @Builder
    public record Product(
            UUID productId,
            Integer quantity
    ) {
        public static Product of(
                UUID productId,
                Integer quantity
        ) {
            return Product.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
        }
    }
}
