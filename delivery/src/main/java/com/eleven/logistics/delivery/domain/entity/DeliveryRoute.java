package com.eleven.logistics.delivery.domain.entity;

import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRouteFromMessageRequest;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRouteRequest;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryRouteRequest;
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

  @Column(name = "delivery_person_id")
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

  // API 통신용 생성자
  public DeliveryRoute(Delivery delivery, CreateDeliveryRouteRequest routeDto) {
    this.delivery = delivery;
    this.sequence = routeDto.getSequence();
    this.departureHubId = routeDto.getDepartureHubId();
    this.arrivalHubId = routeDto.getArrivalHubId();
    this.expectedDistance = routeDto.getExpectedDistance();
    this.expectedTime = routeDto.getExpectedTime();
    this.routeStatus = RouteStatus.WAITING_FOR_HUB_MOVING;
    this.actualDistance = 0;
    this.actualTime = 0;
  }

  // 메시지 처리용 생성자
  public DeliveryRoute(Delivery delivery, CreateDeliveryRouteFromMessageRequest request) {
    this.id = UUID.randomUUID();
    this.delivery = delivery;
    this.sequence = request.getSequence();
    this.departureHubId = request.getDepartureHubId();
    this.arrivalHubId = request.getArrivalHubId();
    this.expectedDistance = request.getExpectedDistance();
    this.expectedTime = request.getExpectedTime();
    this.routeStatus = RouteStatus.WAITING_FOR_HUB_MOVING; // 초기 상태
    this.actualDistance = 0;
    this.actualTime = 0;
  }

  public void updateRoute(UpdateDeliveryRouteRequest routeDto) {
    this.departureHubId = routeDto.getDepartureHubId();
    this.arrivalHubId = routeDto.getArrivalHubId();
    this.actualDistance = routeDto.getDistance();
    this.actualTime = routeDto.getTime();

    // status 문자열을 RouteStatus로 변환하여 routeStatus 변경
    if (routeDto.getStatus() != null) {
      this.routeStatus = getRouteStatusFromDescription(routeDto.getStatus());
    }
  }

  // status 값을 description에 맞는 RouteStatus로 변환하는 메서드
  private RouteStatus getRouteStatusFromDescription(String statusDescription) {
    for (RouteStatus status : RouteStatus.values()) {
      if (status.getDescription().equals(statusDescription)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid route status description: " + statusDescription);
  }

  public void updateActualTimeAndDistance(RouteStatus status, int actualDistance, int actualTime) {
    this.routeStatus = status;
    this.actualDistance = actualDistance;
    this.actualTime = actualTime;
  }

  public void updateRouteStatus(RouteStatus status) {
    this.routeStatus = status;
  }

  public void assignDelivery(Delivery delivery) {
    this.delivery = delivery;
  }

  public void updateDeliveryPerson(UUID id) {
    this.deliveryPersonId = id;
  }
}
