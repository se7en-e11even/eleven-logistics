package com.eleven.logistics.delivery.application.dtos;

import com.eleven.logistics.delivery.domain.entity.DeliveryRoute;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteResponse {
  private UUID id;
  private UUID deliveryId;
  private UUID deliveryPersonId;
  private int sequence;
  private UUID departureHubId;
  private UUID arrivalHubId;
  private int expectedDistance;
  private int expectedTime;
  private int actualDistance;
  private int actualTime;
  private RouteStatus routeStatus;

  public DeliveryRouteResponse(DeliveryRoute route) {
    this.id = route.getId();
    this.deliveryId = route.getDelivery().getId();
    this.deliveryPersonId = route.getDeliveryPersonId();
    this.sequence = route.getSequence();
    this.departureHubId = route.getDepartureHubId();
    this.arrivalHubId = route.getArrivalHubId();
    this.expectedDistance = route.getExpectedDistance();
    this.expectedTime = route.getExpectedTime();
    this.actualDistance = route.getActualDistance();
    this.actualTime = route.getActualTime();
    this.routeStatus = route.getRouteStatus();
  }
}
