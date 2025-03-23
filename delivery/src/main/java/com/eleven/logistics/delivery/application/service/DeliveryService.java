package com.eleven.logistics.delivery.application.service;

import com.eleven.logistics.delivery.domain.entity.*;
import com.eleven.logistics.delivery.domain.repository.DeliveryPersonRepository;
import com.eleven.logistics.delivery.application.DeliveryEventPublisher;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRouteRequest;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryRouteResponse;
import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryRoute;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryRouteRequest;
import com.eleven.logistics.delivery.util.PagingUtil;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final DeliveryPersonRepository deliveryPersonRepository;
  private final DeliveryEventPublisher eventPublisher;

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

  // 배송 검색 조회
  public Page<DeliveryResponse> searchDeliveries(
      DeliveryStatus status, UUID orderId, Pageable pageable
  ) {
    Pageable deliveryPages = PagingUtil.adjustPageable(pageable);
    return deliveryRepository.findByDeliveryStatusAndOrderId(status, orderId, deliveryPages)
        .map(DeliveryResponse::new);
  }

  // 배송경로 검색 조회
  public Page<DeliveryRouteResponse> searchDeliveryRoutes(
      UUID deliveryId, RouteStatus status, Pageable pageable
  ) {
    Pageable routePages = PagingUtil.adjustPageable(pageable);
    return deliveryRepository.findRoutesByDeliveryIdAndRouteStatus(deliveryId, status, routePages)
        .map(DeliveryRouteResponse::new);
  }

  // 배송 생성
  @Transactional
  public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
    Delivery delivery = deliveryRepository.save(new Delivery(request));

    deliveryRepository.save(delivery);
    delivery.updateCreatedBy(delivery.getCreatedBy());

    return new DeliveryResponse(delivery);
  }

  // 배송 경로 생성
  @Transactional
  public List<UUID> createRoute(
      UUID deliveryId, List<CreateDeliveryRouteRequest> routeDtos
  ) {
    if (routeDtos == null || routeDtos.isEmpty()) {
      throw new IllegalArgumentException(
          "At least one route must be provided when creating a delivery");
    }

    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    // 기존 경로 개수 저장
    int existingRouteCount = delivery.getRoutes().size();

    List<UUID> routeIdList = new ArrayList<>(); {
    }

    // DeliveryRoute 객체 생성 및 Delivery에 추가
    routeDtos.forEach(routeDto -> {
      DeliveryRoute route = new DeliveryRoute(delivery, routeDto);
      delivery.addRoute(route);
      route.updateCreatedBy(delivery.getCreatedBy());
      DeliveryPerson dp = deliveryPersonRepository.findBySequence(routeDto.getSequence())
              .orElseThrow(() -> new IllegalArgumentException("delivery Person not found"));
      route.updateDeliveryPerson(dp.getId());
      routeIdList.add(route.getId());
      // 로그로 모든 필드 출력 (하나의 로그로 합침)
        String logMessage = "Created DeliveryRoute: " +
                "Route ID: " + route.getId() + ", " +
                "Delivery ID: " + route.getDelivery().getId() + ", " +
                "Sequence: " + route.getSequence() + ", " +
                "Departure Hub ID: " + route.getDepartureHubId() + ", " +
                "Arrival Hub ID: " + route.getArrivalHubId() + ", " +
                "Expected Distance: " + route.getExpectedDistance() + ", " +
                "Expected Time: " + route.getExpectedTime() + ", " +
                "Actual Distance: " + route.getActualDistance() + ", " +
                "Actual Time: " + route.getActualTime() + ", " +
                "Route Status: " + route.getRouteStatus().getDescription() + ", " +
                "Delivery Person ID: " + route.getDeliveryPersonId();
      log.info(logMessage);
    });

    deliveryRepository.save(delivery);

    // 저장된 routes에서 새로 추가된 경로만 추출
    List<DeliveryRoute> savedRoutes = delivery.getRoutes().subList(
        existingRouteCount, delivery.getRoutes().size()
    );

//    return savedRoutes.stream()
//        .map(CreateDeliveryRouteResponse::new)
//        .collect(Collectors.toList());

    return routeIdList;
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
      UpdateDeliveryRouteRequest routeDto
  ) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    DeliveryRoute route = delivery.getDeliveryRoutes().stream()
        .filter(r -> r.getId().equals(routeId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Route not found"));

    // 새로운 값으로 경로 업데이트
    route.updateRoute(routeDto);
    // 로그로 모든 필드 출력 (하나의 로그로 합침)
    String logMessage = "Updated DeliveryRoute: " +
            "Route ID: " + route.getId() + ", " +
            "Delivery ID: " + route.getDelivery().getId() + ", " +
            "Sequence: " + route.getSequence() + ", " +
            "Departure Hub ID: " + route.getDepartureHubId() + ", " +
            "Arrival Hub ID: " + route.getArrivalHubId() + ", " +
            "Expected Distance: " + route.getExpectedDistance() + ", " +
            "Expected Time: " + route.getExpectedTime() + ", " +
            "Actual Distance: " + route.getActualDistance() + ", " +
            "Actual Time: " + route.getActualTime() + ", " +
            "Route Status: " + route.getRouteStatus().getDescription() + ", " +
            "Delivery Person ID: " + route.getDeliveryPersonId();
    log.info(logMessage);
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

}
