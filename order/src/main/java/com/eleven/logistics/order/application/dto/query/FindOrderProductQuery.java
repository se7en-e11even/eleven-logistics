package com.eleven.logistics.order.application.dto.query;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record FindOrderProductQuery(
        UUID orderProductId,
        UUID productId,
        Integer price,
        Integer quantity
) {
    public static FindOrderProductQuery create(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
        return FindOrderProductQuery.builder()
                .orderProductId(orderProductId)
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
