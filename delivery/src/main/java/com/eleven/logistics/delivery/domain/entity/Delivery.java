package com.eleven.logistics.delivery.domain.entity;

import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
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
@Table(name = "p_delivery")
public class Delivery {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "delivery_id", nullable = false)
  private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "departure_hub_id", nullable = false)
  private UUID departureHubId;

  @Column(name = "destination_hub_id", nullable = false)
  private UUID destinationHubId;

  @Column(name = "delivery_address", nullable = false)
  private String deliveryAddress;

  @Column(name = "receiver", nullable = false)
  private String receiver;

  @Column(name = "receiver_sns_id", nullable = false)
  private UUID receiverSnsId;

  @Column(name = "company_delivery_manager_id", nullable = false)
  private UUID companyDeliveryManagerId;

  @Column(name = "delivery_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private DeliveryStatus deliveryStatus;

  public Delivery(CreateDeliveryRequest request) {
    this.orderId = request.getOrderId();
    this.departureHubId = request.getDepartureHubId();
    this.destinationHubId = request.getDestinationHubId();
    this.deliveryAddress = request.getDeliveryAddress();
    this.receiver = request.getReceiver();
    this.receiverSnsId = request.getReceiverSnsId();
    this.companyDeliveryManagerId = request.getCompanyDeliveryManagerId();
    this.deliveryStatus = DeliveryStatus.PENDING_AT_HUB;
  }

  public void update(UpdateDeliveryRequest request) {
    this.deliveryAddress = request.getDeliveryAddress();
    this.receiver = request.getReceiver();
    this.receiverSnsId = request.getReceiverSnsId();
    this.companyDeliveryManagerId = request.getCompanyDeliveryManagerId();
    this.deliveryStatus = request.getDeliveryStatus();
  }
}
