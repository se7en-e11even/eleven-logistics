package com.eleven.logistics.order.application.dto.query;

import java.util.List;

public record ListOrderQuery<T> (
        List<T> result,
        long totalElements
) {
}
