package com.eleven.logistics.delivery.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DeliveryPersonType {
  HUB_DELIVERY_PERSON("허브 배송 담당자"),
  COMPANY_DELIVERY_PERSON("업체 배송 담당자");

  private final String description;

  DeliveryPersonType(String description) {
    this.description = description;
  }
}
