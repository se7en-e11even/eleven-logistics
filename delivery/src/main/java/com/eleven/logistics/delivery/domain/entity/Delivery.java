package com.eleven.logistics.delivery.domain.entity;

import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_deliveries")
public class Delivery extends Timestamped {

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

  @Column(name = "company_delivery_person_id", nullable = false)
  private UUID companyDeliveryPersonId;

  @Column(name = "delivery_status")
  @Enumerated(EnumType.STRING)
  private DeliveryStatus deliveryStatus;

  @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

  public Delivery(CreateDeliveryRequest request) {
    this.orderId = request.getOrderId();
    this.departureHubId = request.getDepartureHubId();
    this.destinationHubId = request.getDestinationHubId();
    this.deliveryAddress = request.getDeliveryAddress();
    this.receiver = request.getReceiver();
    this.receiverSnsId = request.getReceiverSnsId();
    this.deliveryStatus = DeliveryStatus.PENDING_AT_HUB;
  }

  public void update(UpdateDeliveryRequest request) {
    this.receiver = request.getReceiver();
    this.receiverSnsId = request.getReceiverSnsId();
    this.companyDeliveryPersonId = request.getCompanyDeliveryPersonId();
  }

  public void updateStatus(DeliveryStatus currentStatus) {
    this.deliveryStatus = currentStatus;
  }

  // 배송 경로 추가
  public void addRoute(DeliveryRoute route) {
    this.deliveryRoutes.add(route);
    route.assignDelivery(this);
  }

  public List<DeliveryRoute> getRoutes() {
    return deliveryRoutes;
  }
}
