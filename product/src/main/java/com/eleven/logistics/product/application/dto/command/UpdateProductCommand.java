package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateProductCommand(
        UUID productId,
        String name,
        Integer price,
        Integer stockQuantity
) {

    public static UpdateProductCommand create(
            UUID productId,
            String name,
            Integer price,
            Integer stockQuantity
    ) {
        return UpdateProductCommand.builder()
                .productId(productId)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
