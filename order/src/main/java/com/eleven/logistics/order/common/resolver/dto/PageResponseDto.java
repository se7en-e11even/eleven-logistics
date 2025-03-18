package com.eleven.logistics.order.common.resolver.dto;

import java.util.List;

public record PageResponseDto<T>(
        List<T> result,
        long totalElements
) {
}
