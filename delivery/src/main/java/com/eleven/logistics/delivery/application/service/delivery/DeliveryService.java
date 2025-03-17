package com.eleven.logistics.delivery.application.service.delivery;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliverySearchResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;

  public DeliveryResponse getDelivery(String username, String userRole, UUID deliveryId) {
    // 사용자 권한 체크 검증로직 추가 예정

    Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
        new IllegalArgumentException("Delivery not found"));

    // 배송담당자 - 본인 배송만 수정 가능, 본인 배송만 조회 및 검색 가능 검증로직 추가 예정

    return new DeliveryResponse(
        deliveryId,
        delivery.getOrderId(),
        delivery.getDepartureHubId(),
        delivery.getDestinationHubId(),
        delivery.getDeliveryAddress(),
        delivery.getReceiver(),
        delivery.getReceiverSnsId(),
        delivery.getCompanyDeliveryManagerId(),
        delivery.getDeliveryStatus());
  }

  public Page<DeliverySearchResponse> getDeliveriesByKeyword(
      String username,
      String userRole,
      UUID orderId,
      UUID departureHubId,
      UUID destinationHubId,
      String keyword,
      DeliveryStatus deliveryStatus,
      int page,
      int size,
      String sortedBy,
      Sort.Direction direction
  ) {
    // 사용자 권한 체크 검증로직 추가 예정

    Pageable pageable = PageRequest.of(page, size, direction, sortedBy);
    Page<Delivery> deliveryPage = deliveryRepository.findAllByKeyword(
        orderId, departureHubId, destinationHubId, keyword, deliveryStatus, pageable);

    // delivery 본인 권한 체크 검증로직 추가 예정

    return deliveryPage.map((delivery ->
        new DeliverySearchResponse(
            delivery.getId(),
            delivery.getOrderId(),
            delivery.getReceiver(),
            delivery.getDeliveryAddress(),
            delivery.getCompanyDeliveryManagerId(),
            delivery.getDeliveryStatus())));
  }

  @Transactional
  public DeliveryResponse createDelivery(String username, String userRole,
      CreateDeliveryRequest request) {
    // 사용자 권한 체크 검증로직 추가 예정

    Delivery delivery = deliveryRepository.save(new Delivery(request));

    delivery.updateCreatedBy(username);
    return new DeliveryResponse(delivery);
  }

  @Transactional
  public DeliveryResponse updateDelivery(String username, String userRole, UUID deliveryId,
      UpdateDeliveryRequest request) {
    // 사용자 권한 체크 검증로직 추가 예정

    // delivery 체크
    Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
        new IllegalArgumentException("Delivery not found"));

    // delivery 본인 권한 체크 검증로직 추가 예정

    delivery.update(request);
    delivery.updateModificationInfo(username);
    deliveryRepository.save(delivery);

    return new DeliveryResponse(delivery);
  }

  @Transactional
  public void deleteDelivery(String username, String userRole, UUID deliveryId) {
    // 사용자 권한 체크 검증로직 추가 예정

    // delivery 체크
    Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
        new IllegalArgumentException("Delivery not found"));

    // delivery 본인 권한 체크 검증로직 추가 예정

    delivery.updateDeletionInfo(username);
    deliveryRepository.save(delivery);
  }
}
