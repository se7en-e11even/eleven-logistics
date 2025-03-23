package com.eleven.logistics.order.application.dto.command;

import java.util.List;
import java.util.UUID;

public record ProductOrderCommand(
        List<OrderProduct> productList
) {
    public record OrderProduct(
            UUID productId,
            Integer quantity
    ) {
    }
}
