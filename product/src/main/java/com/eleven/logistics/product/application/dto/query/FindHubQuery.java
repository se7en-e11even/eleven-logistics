package com.eleven.logistics.product.application.dto.query;

import java.util.UUID;

/**
 * FeignClient 를 사용해 Hub 정보를 요청하는 데 사용할 DTO
 */
public record FindHubQuery(
        int code,
        String message,
        Company data
) {

    public record Company(
            String name,
            String address,
            String type,
            UUID hubId,
            String username
    ) {
    }
}
