package com.eleven.logistics.delivery.presentation.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryRequest {

  @NotNull
  private String receiver;

  @NotNull
  private String receiverSnsId;

  private UUID companyDeliveryPersonId;

}
