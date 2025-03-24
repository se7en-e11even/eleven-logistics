package com.eleven.logistics.product.presentation.dto.request;

import com.eleven.logistics.product.application.dto.command.OrderProductCommand;

import java.util.List;
import java.util.UUID;

public record OrderProductRequest(
        List<Product> productList
) {

    public OrderProductCommand toCommand() {
        return OrderProductCommand.of(
                productList.stream()
                        .map(OrderProductRequest.Product::toCommand)
                        .toList()
        );
    }

    public record Product(
            UUID productId,
            Integer quantity
    ) {
        public OrderProductCommand.Product toCommand() {
            return OrderProductCommand.Product.of(
                    productId,
                    quantity
            );
        }
    }
}
