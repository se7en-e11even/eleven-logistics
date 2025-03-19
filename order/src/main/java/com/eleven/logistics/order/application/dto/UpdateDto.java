package com.eleven.logistics.order.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateDto(
        UUID orderId,
        String orderStatus,
        String request,
        List<OrderProductDto> orderProductList
) {
    public static UpdateDto create(
            UUID orderId,
            String orderStatus,
            String request,
            List<OrderProductDto> orderProductList
    ) {
        return UpdateDto.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .request(request)
                .orderProductList(orderProductList)
                .build();
    }
}
