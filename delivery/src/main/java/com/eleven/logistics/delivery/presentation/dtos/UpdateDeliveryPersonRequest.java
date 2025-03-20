package com.eleven.logistics.delivery.presentation.dtos;

import com.eleven.logistics.delivery.domain.entity.DeliveryPersonType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryPersonRequest {

  @NotNull
  private UUID snsId;

  @NotNull
  private UUID hubId;

  private DeliveryPersonType deliveryPersonType;
}
