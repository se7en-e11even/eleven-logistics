package com.eleven.logistics.delivery.presentation.dtos.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequest {

  @NotBlank
  private UUID orderId;

  @NotBlank
  private UUID departureHubId;

  @NotBlank
  private UUID destinationHubId;

  @NotBlank
  private String deliveryAddress;

  @NotBlank
  private String receiver;

  @NotBlank
  private UUID receiverSnsId;

  @NotBlank
  private UUID companyDeliveryManagerId;

}
