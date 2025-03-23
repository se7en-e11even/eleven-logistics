package com.eleven.logistics.order.presentation.dto.request;

import com.eleven.logistics.order.application.dto.command.UpdateOrderCommand;
import com.eleven.logistics.order.application.dto.command.UpdateOrderProductCommand;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record UpdateOrderRequest(
        String orderStatus,
        String request,
        List<UpdateOrderProductRequest> updateOrderProductRequestList
) {
    public UpdateOrderCommand toCommandWithId(UUID orderId) {
        return UpdateOrderCommand.of(
                orderId,
                orderStatus,
                request,
                updateOrderProductRequestList.stream()
                        .map(UpdateOrderProductRequest::toCommand)
                        .toList()
        );
    }

    @Builder
    public record UpdateOrderProductRequest(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
        public static UpdateOrderProductCommand toCommand(
                UpdateOrderProductRequest request
        ) {
            return UpdateOrderProductCommand.of(
                    request.orderProductId,
                    request.productId,
                    request.price,
                    request.quantity
            );
        }
    }
}
