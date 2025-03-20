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

    public static CreateOrderCommand create(
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
}
