package com.eleven.logistics.order.application.dto.command;

import lombok.Builder;

public record ListOrderCommand(
        Integer page,
        Integer size,
        String orderBy
) {

    @Builder
    public ListOrderCommand(Integer page, Integer size, String orderBy) {
        this.page = page;
        this.size = size;
        this.orderBy = orderBy;
    }

    public static ListOrderCommand of(Integer page, Integer size, String orderBy) {
        return ListOrderCommand.builder()
                .page(page)
                .size(size)
                .orderBy(orderBy)
                .build();
    }

    public static ListOrderCommand of(Integer page, Integer size) {
        return ListOrderCommand.builder()
                .page(page)
                .size(size)
                .build();
    }

    public Long getFirstIndex() {
        return Integer.toUnsignedLong(this.page * this.size);
    }
}
