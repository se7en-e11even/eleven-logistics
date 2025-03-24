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

  public static DeliveryToSlackMessage toSlack(UUID id, String username) {
    return new DeliveryToSlackMessage(id, username);
  }
}
