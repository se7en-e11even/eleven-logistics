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
  private String errorMessage;

  public static DeliveryToHubRouteMessage toHubRoute(
      UUID deliveryId, UUID departureHubId, UUID destinationHubId) {
    return new DeliveryToHubRouteMessage(deliveryId, departureHubId, destinationHubId, "No error occurred.");
  }

  public static DeliveryToHubRouteMessage withError(
      UUID deliveryId, UUID departureHubId, UUID destinationHubId, String errorMessage) {
    return new DeliveryToHubRouteMessage(deliveryId, departureHubId, destinationHubId, errorMessage);
  }

}
