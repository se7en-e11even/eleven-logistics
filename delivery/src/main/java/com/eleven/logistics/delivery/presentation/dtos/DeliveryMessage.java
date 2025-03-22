package com.eleven.logistics.delivery.presentation.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryMessage {

  private UUID deliveryId;
  private UUID originHubId;
  private UUID destinationHubId;
  private String deliveryStatus;
  private String username;

}
