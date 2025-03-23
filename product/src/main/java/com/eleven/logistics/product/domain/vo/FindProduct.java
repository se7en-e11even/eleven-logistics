package com.eleven.logistics.product.domain.vo;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindProduct(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        int price,
        int stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
