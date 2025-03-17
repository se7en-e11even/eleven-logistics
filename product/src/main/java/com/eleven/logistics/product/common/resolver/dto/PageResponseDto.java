package com.eleven.logistics.product.common.resolver.dto;

import java.util.List;

public record PageResponseDto<T>(
        List<T> result,
        long totalElements
) {
}
