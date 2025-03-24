package com.eleven.logistics.slack.application.deliverydto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonResponse {

  private UUID id;
  private String username;
}
