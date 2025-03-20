package com.eleven.logistics.product.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateProductCommand(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer stockQuantity
) {

    public static UpdateProductCommand create(
            UUID productId,
            UUID companyId,
            UUID hubId,
            String name,
            Integer price,
            Integer stockQuantity
    ) {
        return UpdateProductCommand.builder()
                .productId(productId)
                .companyId(companyId)
                .hubId(hubId)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
