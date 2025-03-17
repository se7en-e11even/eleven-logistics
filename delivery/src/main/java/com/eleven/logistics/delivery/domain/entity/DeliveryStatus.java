package com.eleven.logistics.delivery.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DeliveryStatus {
  PENDING_AT_HUB("허브 대기 중"),
  IN_TRANSIT_TO_HUB_("허브 이동 중"),
  ARRIVED_AT_DESTINATION_HUB("목적지 허브 도착"),
  OUT_FOR_DELIVERY("배송 중"),
  IN_TRANSIT_TO_COMPANY("업체 이동 중"),
  DELIVERED("배송 완료");

  private final String description;

  DeliveryStatus(String description) {
    this.description = description;
  }
}
