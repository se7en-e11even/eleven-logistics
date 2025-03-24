package com.eleven.logistics.order.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record CreateOrderCommand(
        String request,
        List<CreateOrderProductCommand> commandList
) {

    public static CreateOrderCommand of(
            String request,
            List<CreateOrderProductCommand> commandList
    ) {
        return CreateOrderCommand.builder()
                .request(request)
                .commandList(commandList)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record CreateOrderProductCommand(
            UUID productId,
            Integer price,
            Integer quantity
    ) {
        public static CreateOrderProductCommand of(
                UUID productId,
                Integer price,
                Integer quantity
        ) {
            return CreateOrderProductCommand.builder()
                    .productId(productId)
                    .price(price)
                    .quantity(quantity)
                    .build();
        }
    }
}
