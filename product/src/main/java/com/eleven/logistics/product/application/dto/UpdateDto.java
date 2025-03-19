package com.eleven.logistics.product.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateDto(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer quantity
) {

    public static UpdateDto create(
            UUID productId,
            UUID companyId,
            UUID hubId,
            String name,
            Integer price,
            Integer quantity
    ) {
        return UpdateDto.builder()
                .productId(productId)
                .companyId(companyId)
                .hubId(hubId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
