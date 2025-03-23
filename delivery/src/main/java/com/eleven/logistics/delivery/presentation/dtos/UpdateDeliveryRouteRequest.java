package com.eleven.logistics.delivery.presentation.dtos;

import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryRouteRequest {

  @NotNull
  private UUID departureHubId;

  @NotNull
  private UUID arrivalHubId;

  private int distance;

  private int time;

  String status;


}
