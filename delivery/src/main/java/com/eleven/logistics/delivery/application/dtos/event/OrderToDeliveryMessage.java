package com.eleven.logistics.delivery.application.dtos.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderToDeliveryMessage {

  private UUID orderId;
  private UUID departureHubId;
  private UUID destinationHubId;
  private String deliveryAddress;
  private String receiver;
  private String receiverSnsId;
  private String errorMessage;
}

