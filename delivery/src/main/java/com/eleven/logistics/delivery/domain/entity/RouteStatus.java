package com.eleven.logistics.delivery.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RouteStatus {
  WAITING_FOR_HUB_MOVING("허브 이동 대기 중"),
  MOVING_TO_HUB("허브 이동 중"),
  ARRIVED_AT_DESTINATION_HUB("허브 도착"),
  IN_DELIVERY("배송 중");

  private final String description;

  RouteStatus(String description) {
    this.description = description;
  }

}
