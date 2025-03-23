package com.eleven.logistics.product.presentation.dto.request;

import com.eleven.logistics.product.application.dto.command.OrderProductCommand;

import java.util.List;
import java.util.UUID;

public record OrderProductRequest(
        List<OrderProduct> productList
) {

    public OrderProductCommand toCommand() {
        return OrderProductCommand.of(
                productList.stream()
                        .map(OrderProduct::toCommand)
                        .toList()
        );
    }

    public record OrderProduct(
            UUID productId,
            Integer quantity
    ) {
        public OrderProductCommand.OrderProduct toCommand() {
            return OrderProductCommand.OrderProduct.of(
                    productId,
                    quantity
            );
        }
    }
}
