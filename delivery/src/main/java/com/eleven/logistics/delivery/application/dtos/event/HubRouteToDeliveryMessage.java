package com.eleven.logistics.delivery.application.dtos.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor

public class HubRouteToDeliveryMessage {

  private UUID deliveryId;
  private String description;
  private int sequence;
  private UUID departureHubId;
  private UUID arrivalHubId;
  private int expectedTime;
  private int expectedDistance;
  private String errType;

}
