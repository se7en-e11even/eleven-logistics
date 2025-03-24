package com.eleven.logistics.delivery.application.dtos.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryToHubRouteMessage {

  private UUID deliveryId;
  private UUID originHubId;
  private UUID destinationHubId;

  public static DeliveryToHubRouteMessage toHubRoute(
      UUID id, UUID departureHubId, UUID destinationHubId) {
    return new DeliveryToHubRouteMessage(id, departureHubId, destinationHubId);
  }
}
