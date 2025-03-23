package com.eleven.logistics.order.application.dto.command;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateOrderCommand(
        UUID orderId,
        String orderStatus,
        String request,
        List<UpdateOrderProductCommand> orderProductList
) {
    public static UpdateOrderCommand of(
            UUID orderId,
            String orderStatus,
            String request,
            List<UpdateOrderProductCommand> orderProductList
    ) {
        return UpdateOrderCommand.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .request(request)
                .orderProductList(orderProductList)
                .build();
    }
}
