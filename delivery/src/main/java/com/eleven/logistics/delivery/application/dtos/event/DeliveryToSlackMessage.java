package com.eleven.logistics.delivery.application.dtos.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryToSlackMessage {

  private UUID deliveryId;
  private String username;
  private String errorMessage;

  public static DeliveryToSlackMessage toSlack(UUID deliveryId, String username) {
    return new DeliveryToSlackMessage(deliveryId, username, "No error occurred.");
  }

  public static DeliveryToSlackMessage withError(UUID deliveryId, String username, String errorMessage) {
    return new DeliveryToSlackMessage(deliveryId, username, errorMessage);
  }
}
