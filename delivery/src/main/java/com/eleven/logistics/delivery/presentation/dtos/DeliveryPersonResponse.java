package com.eleven.logistics.delivery.presentation.dtos;

import com.eleven.logistics.delivery.domain.entity.DeliveryPerson;
import com.eleven.logistics.delivery.domain.entity.DeliveryPersonType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonResponse {

  private UUID id;
  private String username;
  private String snsId;
  private UUID hubId;
  private DeliveryPersonType deliveryPersonType;

  public DeliveryPersonResponse(DeliveryPerson deliveryPerson) {
    this.id = deliveryPerson.getId();
    this.username = deliveryPerson.getUsername();
    this.snsId = deliveryPerson.getSnsId();
    this.hubId = deliveryPerson.getHubId();
    this.deliveryPersonType = deliveryPerson.getDeliveryPersonType();
  }

}
