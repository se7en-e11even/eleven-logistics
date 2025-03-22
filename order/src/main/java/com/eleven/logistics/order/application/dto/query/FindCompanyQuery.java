package com.eleven.logistics.order.application.dto.query;

import java.util.UUID;

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
