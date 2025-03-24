package com.eleven.logistics.product.application.dto.query;

import java.util.List;
import java.util.UUID;

/**
 * FeignClient 를 사용해 Hub 정보를 요청하는 데 사용할 DTO
 */
public record FindHubQuery(
        int code,
        String message,
        Hub data
) {

    public record Hub(
            UUID id,
            String name,
            String address,
            double longitude,
            double latitude,
            PageResponseDto companyList
    ) {
    }

    public record PageResponseDto(
            List<Company> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {

    }

    public record Company(
            UUID id,
            String name,
            String address,
            String type,
            UUID hubId,
            String username
    ) {

    }
}
