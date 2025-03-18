package com.eleven.logistics.order.application.dto;

import com.eleven.logistics.order.domain.entity.OrderProduct;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderProductDto(
        UUID productId,
        int price,
        int quantity
) {
    public static OrderProductDto create(
            UUID productId,
            int price,
            int quantity
    ) {
        return OrderProductDto.builder()
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .build();
    }

    public OrderProduct toEntity() {
        return OrderProduct.builder()
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
