package com.eleven.logistics.delivery.application.dtos.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OrderMessage(
    UUID orderId,
    UUID departureHubId,
    UUID destinationHubId,

    String deliveryAddress,
    String receiver,
    String receiverSnsId,
    String errType
) {

  public static OrderMessage of(
      UUID orderId,
      UUID departureHubId,
      UUID destinationHubId,

      String deliveryAddress,
      String receiver,
      String receiverSnsId,
      String errType
  ) {
    return OrderMessage.builder()
        .orderId(orderId)
        .departureHubId(departureHubId)
        .destinationHubId(destinationHubId)
        .deliveryAddress(deliveryAddress)
        .receiver(receiver)
        .receiverSnsId(receiverSnsId)
        .errType(errType)
        .build();
  }
}