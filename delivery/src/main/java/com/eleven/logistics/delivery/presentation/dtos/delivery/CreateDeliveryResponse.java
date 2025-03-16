package com.eleven.logistics.delivery.presentation.dtos.delivery;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryResponse {

  private UUID id;
  private UUID orderId;
  private UUID departureHubId;
  private UUID destinationHubId;
  private String deliveryAddress;
  private String receiver;
  private UUID receiverSnsId;
  private UUID companyDeliveryManagerId;
  private DeliveryStatus deliveryStatus;

  public CreateDeliveryResponse(Delivery delivery) {
    this.id = delivery.getId();
    this.orderId = delivery.getOrderId();
    this.departureHubId = delivery.getDepartureHubId();
    this.destinationHubId = delivery.getDestinationHubId();
    this.deliveryAddress = delivery.getDeliveryAddress();
    this.receiver = delivery.getReceiver();
    this.receiverSnsId = delivery.getReceiverSnsId();
    this.deliveryStatus = delivery.getDeliveryStatus();
  }
}
