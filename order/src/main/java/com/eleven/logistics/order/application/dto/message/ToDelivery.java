package com.eleven.logistics.order.application.dto.message;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record ToDelivery(
        UUID orderId,

        UUID departureHubId,
        UUID destinationHubId,

        String deliveryAddress,
        String receiver,
        String receiverSnsId
) {
    public static ToDelivery of(
            UUID orderId,
            UUID departureHubId,
            UUID destinationHubId,

            String deliveryAddress,
            String receiver,
            String receiverSnsId
    ) {
        return ToDelivery.builder()
                .orderId(orderId)
                .departureHubId(departureHubId)
                .destinationHubId(destinationHubId)
                .deliveryAddress(deliveryAddress)
                .receiver(receiver)
                .receiverSnsId(receiverSnsId)
                .build();
    }
}
