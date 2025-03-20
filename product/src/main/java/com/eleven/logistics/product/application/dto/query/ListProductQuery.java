package com.eleven.logistics.product.application.dto.query;

import java.util.List;

public record ListProductQuery<T>(
        List<T> result,
        long totalElements
) {
}

