package com.eleven.logistics.delivery.presentation.dtos.delivery;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRequest {

  @NotNull
  private UUID deliveryPersonId;

  private int sequence;

  @NotNull
  private UUID departureHubId;

  @NotNull
  private UUID arrivalHubId;

  @NotNull
  private int expectedDistance;

  @NotNull
  private int expectedTime;
}
