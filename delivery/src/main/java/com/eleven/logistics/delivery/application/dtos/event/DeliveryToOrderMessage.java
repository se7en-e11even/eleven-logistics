package com.eleven.logistics.delivery.application.dtos.event;

import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryToOrderMessage {

  private UUID deliveryId;
  private String deliveryStatus;

  public static DeliveryToOrderMessage toOrder(UUID deliveryId, DeliveryStatus deliveryStatus) {
    return new DeliveryToOrderMessage(deliveryId, deliveryStatus.getDescription());
  }
}
