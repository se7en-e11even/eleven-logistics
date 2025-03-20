package com.eleven.logistics.delivery.application.service;

import com.eleven.logistics.delivery.presentation.dtos.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryRouteResponse;
import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryRoute;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import com.eleven.logistics.delivery.util.PagingUtil;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryRouteRequest;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;

  // 배송 검색 조회
  public Page<DeliveryResponse> searchDeliveries(
      DeliveryStatus status, UUID orderId, Pageable pageable
  ) {
    // TODO : 사용자 권한 체크 검증로직 추가

    Pageable deliveryPages = PagingUtil.adjustPageable(pageable);
    return deliveryRepository.findByDeliveryStatusAndOrderId(status, orderId, deliveryPages)
        .map(DeliveryResponse::new);
  }

  // 배송경로 검색 조회
  public Page<DeliveryRouteResponse> searchDeliveryRoutes(
      UUID deliveryId, RouteStatus status, Pageable pageable
  ) {
    // TODO : 사용자 권한 체크 검증로직 추가

    Pageable routePages = PagingUtil.adjustPageable(pageable);
    return deliveryRepository.findRoutesByDeliveryIdAndRouteStatus(deliveryId, status, routePages)
        .map(DeliveryRouteResponse::new);
  }

  // 배송 및 배송경로 생성
  @Transactional
  public DeliveryResponse createDelivery(DeliveryRequest deliveryDto,
      List<DeliveryRouteRequest> routeDtos
  ) {
    if (routeDtos == null || routeDtos.isEmpty()) {
      throw new IllegalArgumentException(
          "At least one route must be provided when creating a delivery");
    }

    Delivery delivery = new Delivery(deliveryDto);

    for (DeliveryRouteRequest routeDto : routeDtos) {
      DeliveryRoute route = new DeliveryRoute(delivery, routeDto);
      delivery.addRoute(route);
    }

    deliveryRepository.save(delivery);
    delivery.updateCreatedBy(delivery.getCreatedBy());
    return new DeliveryResponse(delivery);
  }

  // 배송 상태 변경
  @Transactional
  public DeliveryResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status
  ) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    delivery.updateStatus(status);
    delivery.updateModificationInfo(delivery.getUpdatedBy());
    return new DeliveryResponse(delivery);
  }

  // 배송경로 상태 변경
  @Transactional
  public DeliveryRouteResponse updateRouteStatus(UUID deliveryId, UUID routeId,
      RouteStatus status, int actualDistance, int actualTime
  ) {
    // TODO : 사용자 권한 체크 검증로직 추가

    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    DeliveryRoute route = delivery.getDeliveryRoutes().stream()
        .filter(r -> r.getId().equals(routeId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Route not found"));

    route.updateStatus(status, actualDistance, actualTime);
    route.updateCreatedBy(delivery.getCreatedBy());
    return new DeliveryRouteResponse(route);
  }

  // 배송 수정
  @Transactional
  public DeliveryResponse updateDelivery(UUID deliveryId, UpdateDeliveryRequest request) {
    // TODO : 사용자 권한 체크 검증로직 추가

    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    delivery.update(request);
    delivery.updateModificationInfo(delivery.getUpdatedBy());
    deliveryRepository.save(delivery);

    return new DeliveryResponse(delivery);
  }

  // 배송경로 전체 수정
  @Transactional
  public DeliveryRouteResponse updateRoute(UUID deliveryId, UUID routeId,
      DeliveryRouteRequest routeDto
  ) {
    // TODO : 사용자 권한 체크 검증로직 추가

    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    DeliveryRoute route = delivery.getDeliveryRoutes().stream()
        .filter(r -> r.getId().equals(routeId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Route not found"));

    // 새로운 값으로 경로 업데이트
    route.updateRoute(routeDto);
    route.updateModificationInfo(route.getUpdatedBy());
    return new DeliveryRouteResponse(route);
  }

  // 배송경로 삭제
  @Transactional
  public void deleteRoute(UUID deliveryId, UUID routeId) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    DeliveryRoute route = delivery.getDeliveryRoutes().stream()
        .filter(r -> r.getId().equals(routeId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Route not found"));

    if (!route.getDelivery().getId().equals(deliveryId)) {
      throw new IllegalArgumentException("Route does not belong to delivery");
    }

    route.updateDeletionInfo(delivery.getDeletedBy());
    deliveryRepository.save(delivery);
  }

  // 배송 삭제
  @Transactional
  public void deleteDelivery(UUID deliveryId) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    delivery.updateDeletionInfo(delivery.getDeletedBy());
  }

  // 배송 조회
  public DeliveryResponse getDelivery(UUID deliveryId) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    return new DeliveryResponse(delivery);
  }

  //배송 경로 조회(특정 배송의 모든 경로)
  public List<DeliveryRouteResponse> getDeliveryRoutes(UUID deliveryId) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    return delivery.getDeliveryRoutes().stream()
        .map(DeliveryRouteResponse::new)
        .toList();
  }


}
