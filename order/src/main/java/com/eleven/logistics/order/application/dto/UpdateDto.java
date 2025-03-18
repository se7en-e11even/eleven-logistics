package com.eleven.logistics.order.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateDto() {
    public static UpdateDto create() {
        return UpdateDto.builder()
                .build();
    }
}
