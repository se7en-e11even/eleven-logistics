package com.eleven.logistics.product.application.dto.command;

import lombok.Builder;

public record ListProductCommand(
        Integer page,
        Integer size,
        String orderBy
) {

    @Builder
    public ListProductCommand(Integer page, Integer size, String orderBy) {
        this.page = page;
        this.size = size;
        this.orderBy = orderBy;
    }

    public static ListProductCommand of(Integer page, Integer size, String orderBy) {
        return ListProductCommand.builder()
                .page(page)
                .size(size)
                .orderBy(orderBy)
                .build();
    }

    public static ListProductCommand of(Integer page, Integer size) {
        return ListProductCommand.builder()
                .page(page)
                .size(size)
                .build();
    }

    public Long getFirstIndex() {
        return Integer.toUnsignedLong(this.page * this.size);
    }
}
