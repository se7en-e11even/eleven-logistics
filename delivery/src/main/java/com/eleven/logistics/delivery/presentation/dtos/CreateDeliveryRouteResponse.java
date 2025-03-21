package com.eleven.logistics.delivery.presentation.dtos;

import com.eleven.logistics.delivery.domain.entity.DeliveryRoute;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRouteResponse {

  private UUID id;
  private UUID deliveryId;
  private int sequence;
  private UUID departureHubId;
  private UUID arrivalHubId;
  private int expectedDistance;
  private int expectedTime;
  private RouteStatus routeStatus;

  public CreateDeliveryRouteResponse(DeliveryRoute route) {
    this.id = route.getId();
    this.deliveryId = route.getDelivery().getId();
    this.sequence = route.getSequence();
    this.departureHubId = route.getDepartureHubId();
    this.arrivalHubId = route.getArrivalHubId();
    this.expectedDistance = route.getExpectedDistance();
    this.expectedTime = route.getExpectedTime();
    this.routeStatus = route.getRouteStatus();
  }
}
