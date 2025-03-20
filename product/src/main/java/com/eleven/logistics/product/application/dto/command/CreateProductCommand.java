package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record CreateProductCommand(
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer quantity
) {
    public static CreateProductCommand create(
            UUID companyId,
            UUID hubId,
            String name,
            Integer price,
            Integer quantity
    ) {
        return CreateProductCommand.builder()
                .companyId(companyId)
                .hubId(hubId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
