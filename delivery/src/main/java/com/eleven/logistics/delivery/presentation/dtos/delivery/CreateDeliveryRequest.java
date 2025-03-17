package com.eleven.logistics.delivery.presentation.dtos.delivery;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequest {

  private UUID orderId;

  private UUID departureHubId;

  private UUID destinationHubId;

  @NotBlank
  private String deliveryAddress;

  @NotBlank
  private String receiver;

  private UUID receiverSnsId;

  private UUID companyDeliveryManagerId;

}
