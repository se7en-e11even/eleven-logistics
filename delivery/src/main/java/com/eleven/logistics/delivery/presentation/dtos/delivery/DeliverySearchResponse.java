package com.eleven.logistics.delivery.presentation.dtos.delivery;

import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliverySearchResponse {

  private UUID deliveryId;
  private UUID orderId;
  private String receiver;
  private String deliveryAddress;
  private UUID companyDeliveryManager;
  private DeliveryStatus deliveryStatus;

}
