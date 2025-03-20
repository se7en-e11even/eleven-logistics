package com.eleven.logistics.order.application.dto.query;

import java.util.UUID;

public record FindProductQuery(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer stockQuantity
) {
}
