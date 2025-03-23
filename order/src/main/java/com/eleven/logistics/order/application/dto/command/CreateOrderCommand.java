package com.eleven.logistics.order.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record CreateOrderCommand(
        UUID supplyId,
        UUID receiverId,
        String request,
        List<CreateOrderProductCommand> commandList
) {

    public static CreateOrderCommand of(
            UUID supplyId,
            UUID receiverId,
            String request,
            List<CreateOrderProductCommand> commandList
    ) {
        return CreateOrderCommand.builder()
                .supplyId(supplyId)
                .receiverId(receiverId)
                .request(request)
                .commandList(commandList)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record CreateOrderProductCommand(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
        public static CreateOrderProductCommand of(
                UUID orderProductId,
                UUID productId,
                Integer price,
                Integer quantity
        ) {
            return CreateOrderProductCommand.builder()
                    .orderProductId(orderProductId)
                    .productId(productId)
                    .price(price)
                    .quantity(quantity)
                    .build();
        }
    }
}
