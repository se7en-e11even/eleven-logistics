package com.eleven.logistics.order.application.dto.command;

import java.util.List;
import java.util.UUID;

public record OrderProductCommand(
        List<Product> productList
) {
    public record Product(
            UUID productId,
            Integer quantity
    ) {
    }
}
