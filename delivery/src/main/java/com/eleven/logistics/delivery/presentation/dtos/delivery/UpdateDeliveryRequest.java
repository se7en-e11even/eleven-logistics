package com.eleven.logistics.delivery.presentation.dtos.delivery;

import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryRequest {

  @NotBlank
  private String deliveryAddress;

  @NotBlank
  private String receiver;

  @NotBlank
  private UUID receiverSnsId;

  @NotBlank
  private UUID companyDeliveryManagerId;

  @NotBlank
  private DeliveryStatus deliveryStatus;
}
