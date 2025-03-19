package com.eleven.logistics.order.presentation.dto;

import com.eleven.logistics.order.application.dto.OrderProductDto;
import com.eleven.logistics.order.application.dto.UpdateDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateRequestDto(
        String orderStatus,
        String request,
        List<OrderProductUpdateDto> orderProductUpdateDtoList
) {
    public UpdateDto withId(UUID orderId) {
        return UpdateDto.create(
                orderId,
                orderStatus,
                request,
                orderProductUpdateDtoList.stream()
                        .map(OrderProductUpdateDto::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Builder
    public record OrderProductUpdateDto(
            UUID orderProductId,
            UUID productId,
            int price,
            int quantity
    ) {
        public static OrderProductDto toDto(
                OrderProductUpdateDto updateDto
        ) {
            return OrderProductDto.create(
                    updateDto.orderProductId,
                    updateDto.productId,
                    updateDto.price,
                    updateDto.quantity
            );
        }
    }
}
