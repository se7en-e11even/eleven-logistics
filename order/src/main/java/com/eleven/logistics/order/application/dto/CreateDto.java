package com.eleven.logistics.order.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record CreateDto(
        UUID supplyId,
        UUID receiverId,
        String request,
        List<OrderProductDto> orderProductDtoList
) {

    public static CreateDto create(
            UUID supplyId,
            UUID receiverId,
            String request,
            List<OrderProductDto> orderProductDtoList
    ) {
        return CreateDto.builder()
                .supplyId(supplyId)
                .receiverId(receiverId)
                .request(request)
                .orderProductDtoList(orderProductDtoList)
                .build();
    }
}
