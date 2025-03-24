package com.eleven.logistics.delivery.domain.entity;

import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryPersonRequest;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryPersonRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_delivery_persons")
public class DeliveryPerson extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "delivery_person_id", nullable = false)
  private UUID id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "slack_id", nullable = false)
  private String snsId;

  @Column(name = "hub_id")
  private UUID hubId;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private DeliveryPersonType deliveryPersonType;

  @Column(name = "sequence")
  private int sequence;

  public DeliveryPerson(CreateDeliveryPersonRequest request) {
    this.username = request.getUsername();
    this.snsId = request.getSnsId();
    this.hubId = request.getHubId();
    this.deliveryPersonType = request.getDeliveryPersonType();
  }

  public void update(UpdateDeliveryPersonRequest request) {
    this.snsId = request.getSnsId();
    this.hubId = request.getHubId();
    this.deliveryPersonType = request.getDeliveryPersonType();
  }
}

