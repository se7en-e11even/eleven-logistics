package com.eleven.logistics.delivery.application.dtos.message;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteMessage {
  private UUID deliveryId;
  private String description;
  private String errType;
}
