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
public class CreateDeliveryPersonRequest {

  @NotNull
  private String username;

  @NotNull
  private String snsId;

  private UUID hubId;

  private DeliveryPersonType deliveryPersonType;
}
