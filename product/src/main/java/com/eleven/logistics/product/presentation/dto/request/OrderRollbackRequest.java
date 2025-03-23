package com.eleven.logistics.product.presentation.dto.request;

import com.eleven.logistics.product.application.dto.command.OrderRollbackCommand;

import java.util.List;
import java.util.UUID;

public record OrderRollbackRequest(
        List<Product> productList
) {

    public OrderRollbackCommand toCommand() {
        return OrderRollbackCommand.of(
                productList.stream()
                        .map(OrderRollbackRequest.Product::toCommand)
                        .toList()
        );
    }

    public record Product(
            UUID productId,
            Integer quantity
    ) {
        public OrderRollbackCommand.Product toCommand() {
            return OrderRollbackCommand.Product.of(
                    productId,
                    quantity
            );
        }
    }
}
