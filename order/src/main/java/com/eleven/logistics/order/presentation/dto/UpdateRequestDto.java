package com.eleven.logistics.order.presentation.dto;

import com.eleven.logistics.order.application.dto.UpdateDto;

import java.util.UUID;

public record UpdateRequestDto() {
    public UpdateDto withId(UUID orderId) {
        return UpdateDto.create(

        );
    }
}
