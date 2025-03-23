package com.eleven.logistics.order.application.dto.message;

import java.util.UUID;

public record FromDeliveryError(
        UUID orderId,
        UUID deliveryId,
        String message
) {
}
