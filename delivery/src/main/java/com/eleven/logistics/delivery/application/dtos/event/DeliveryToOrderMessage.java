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
  private UUID orderId;
  private String status;
  private String errorMessage;

  public static DeliveryToOrderMessage toOrder(UUID deliveryId,UUID orderId, String status) {
    return new DeliveryToOrderMessage(deliveryId,orderId, status, "No error occurred.");
  }

  public static DeliveryToOrderMessage withError(UUID deliveryId, UUID orderId, String status, String errorMessage) {
    return new DeliveryToOrderMessage(deliveryId,orderId, status, errorMessage);
  }
}
