package com.eleven.logistics.order.application.dto.message;

import java.util.UUID;

public record OrderMessage(
        UUID orderId
) {

}
