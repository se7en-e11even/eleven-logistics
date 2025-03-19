package com.eleven.logistics.product.common.resolver.dto;

import lombok.Builder;

public record PageRequestDto(
        int page,
        int size,
        String orderBy
) {

    @Builder
    public PageRequestDto(int page, int size, String orderBy) {
        this.page = page;
        this.size = size;
        this.orderBy = orderBy;
    }

    public static PageRequestDto of(int page, int size, String orderBy) {
        return PageRequestDto.builder()
                .page(page)
                .size(size)
                .orderBy(orderBy)
                .build();
    }

    public static PageRequestDto of(int page, int size) {
        return PageRequestDto.builder()
                .page(page)
                .size(size)
                .build();
    }

    public long getFirstIndex() {
        return (long) this.page * this.size;
    }
}
