package com.eleven.logistics.delivery.presentation.dtos.delivery;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

  @NotNull
  private UUID orderId;

  @NotNull
  private UUID departureHubId;

  @NotNull
  private UUID destinationHubId;

  @NotNull
  private String deliveryAddress;

  @NotNull
  private String receiver;

  @NotNull
  private UUID receiverSnsId;

  @NotNull
  private UUID companyDeliveryManagerId;
}
