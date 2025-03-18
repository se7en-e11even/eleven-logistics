package com.eleven.logistics.product.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record CreateDto(
        UUID companyId,
        UUID hubId,
        String name,
        int price,
        int quantity
) {
    public static CreateDto create(
            UUID companyId,
            UUID hubId,
            String name,
            int price,
            int quantity
    ) {
        return CreateDto.builder()
                .companyId(companyId)
                .hubId(hubId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
