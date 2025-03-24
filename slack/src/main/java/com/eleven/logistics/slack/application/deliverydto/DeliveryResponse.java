package com.eleven.logistics.slack.application.deliverydto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryResponse {

  private UUID id;
  private UUID orderId;
  private UUID departureHubId;
  private UUID destinationHubId;
  private String deliveryAddress;
  private String receiver;
  private UUID receiverSnsId;
  private UUID companyDeliveryManagerId;

}
