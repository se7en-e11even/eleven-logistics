package com.eleven.logistics.delivery.application.dtos.message;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessage {

  private UUID orderId;
  private String errType;
}
