package com.eleven.logistics.delivery.domain.entity;

import com.eleven.logistics.delivery.presentation.dtos.DeliveryRouteRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_delivery_routes")
public class DeliveryRoute extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "delivery_route_id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id", nullable = false)
  private Delivery delivery;

  @Column(name = "delivery_person_id", nullable = false)
  private UUID deliveryPersonId;

  @Column(name = "sequence", nullable = false)
  private int sequence;

  @Column(name = "departure_hub_id", nullable = false)
  private UUID departureHubId;

  @Column(name = "arrival_hub_id", nullable = false)
  private UUID arrivalHubId;

  @Column(name = "expected_distance")
  private int expectedDistance;

  @Column(name = "expected_time")
  private int expectedTime;

  @Column(name = "actual_distance")
  private int actualDistance;

  @Column(name = "actual_time")
  private int actualTime;

  @Column(name = "route_status")
  @Enumerated(EnumType.STRING)
  private RouteStatus routeStatus;

  public DeliveryRoute(Delivery delivery, DeliveryRouteRequest routeDto) {
    this.delivery = delivery;
    this.deliveryPersonId = routeDto.getDeliveryPersonId();
    this.sequence = routeDto.getSequence();
    this.departureHubId = routeDto.getDepartureHubId();
    this.arrivalHubId = routeDto.getArrivalHubId();
    this.expectedDistance = routeDto.getExpectedDistance();
    this.expectedTime = routeDto.getExpectedTime();
    this.routeStatus = RouteStatus.WAITING_FOR_HUB_MOVING;
    this.actualDistance = 0;
    this.actualTime = 0;
  }

  public void updateRoute(DeliveryRouteRequest routeDto) {
    this.deliveryPersonId = routeDto.getDeliveryPersonId();
    this.sequence = routeDto.getSequence();
    this.departureHubId = routeDto.getDepartureHubId();
    this.arrivalHubId = routeDto.getArrivalHubId();
    this.expectedDistance = routeDto.getExpectedDistance();
    this.expectedTime = routeDto.getExpectedTime();
  }

  public void updateStatus(RouteStatus status, int actualDistance, int actualTime) {
    this.routeStatus = status;
    this.actualDistance = actualDistance;
    this.actualTime = actualTime;
  }

  public void assignDelivery(Delivery delivery) {
    this.delivery = delivery;
  }
}
