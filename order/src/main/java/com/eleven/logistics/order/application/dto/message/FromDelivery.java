package com.eleven.logistics.order.application.dto.message;

import java.util.UUID;

public record FromDelivery(
        UUID orderId,
        UUID deliveryId,
        String status
) {
}
