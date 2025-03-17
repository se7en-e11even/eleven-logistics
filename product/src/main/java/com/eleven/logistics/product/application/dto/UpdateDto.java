package com.eleven.logistics.product.application.dto;

import java.util.UUID;

public record UpdateDto(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer quantity
) {
}
