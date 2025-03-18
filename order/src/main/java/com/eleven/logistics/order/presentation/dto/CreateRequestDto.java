package com.eleven.logistics.order.presentation.dto;

import com.eleven.logistics.order.application.dto.CreateDto;
import com.eleven.logistics.order.application.dto.OrderProductDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateRequestDto(
        UUID supplyId,
        UUID receiverId,
        String request,
        List<OrderProductCreateDto> orderProductCreateDtoList
) {

    public CreateDto toDto() {
        return CreateDto.create(
                supplyId,
                receiverId,
                request,
                orderProductCreateDtoList.stream()
                        .map(OrderProductCreateDto::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Builder
    public record OrderProductCreateDto(
            UUID productId,
            int price,
            int quantity
    ) {
        public static OrderProductDto toDto(OrderProductCreateDto createDto) {
            return OrderProductDto.create(
                    null,
                    createDto.productId,
                    createDto.price,
                    createDto.quantity
            );
        }
    }
}
