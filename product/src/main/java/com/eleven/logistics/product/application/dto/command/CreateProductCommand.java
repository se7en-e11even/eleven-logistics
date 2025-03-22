package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CreateProductCommand(
        String name,
        Integer price,
        Integer quantity
) {
    public static CreateProductCommand of(
            String name,
            Integer price,
            Integer quantity
    ) {
        return CreateProductCommand.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
