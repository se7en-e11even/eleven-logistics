package com.eleven.logistics.product.application.dto.query;

import java.util.UUID;

/**
 * FeignClient 를 사용해 Company 정보를 요청하는 데 사용할 DTO
 */
public record FindCompanyQuery(
        int code,
        String message,
        Company data
) {

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
