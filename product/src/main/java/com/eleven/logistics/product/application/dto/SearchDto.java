package com.eleven.logistics.product.application.dto;

public record SearchDto(
        String name,
        Double minPrice,
        Double maxPrice,
        Integer minQuantity,
        Integer maxQuantity
) {
}
