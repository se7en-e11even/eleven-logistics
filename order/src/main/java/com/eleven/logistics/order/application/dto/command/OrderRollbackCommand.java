package com.eleven.logistics.order.application.dto.command;

import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderRollbackCommand(
        List<Product> productList
) {

    public static OrderRollbackCommand from(Order order) {
        List<Product> productList = order.getOrderProductList().stream()
                .map(Product::from)
                .toList();
        return OrderRollbackCommand.builder()
                .productList(productList)
                .build();
    }

    @Builder
    public record Product(
            UUID productId,
            Integer quantity
    ) {
       public static Product from(OrderProduct orderProduct) {
           return Product.builder()
                   .productId(orderProduct.getProductId())
                   .quantity(orderProduct.getQuantity())
                   .build();
       }
    }
}
