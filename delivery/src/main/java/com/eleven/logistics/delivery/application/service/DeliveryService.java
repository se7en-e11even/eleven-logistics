package com.eleven.logistics.delivery.application.service;

  import com.eleven.logistics.delivery.application.dtos.event.DeliveryToHubRouteMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToOrderMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToSlackMessage;
import com.eleven.logistics.delivery.application.dtos.event.HubRouteToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderToDeliveryMessage;
import com.eleven.logistics.delivery.domain.entity.*;
import com.eleven.logistics.delivery.domain.repository.DeliveryPersonRepository;
import com.eleven.logistics.delivery.application.event.DeliveryEventPublisher;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryRouteFromMessageRequest;
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

    // 메시지 발행
    DeliveryToOrderMessage orderMessage =
        DeliveryToOrderMessage.toOrder(delivery.getId(), delivery.getDeliveryStatus());
    eventPublisher.sendMessagesToOrder(orderMessage);

    DeliveryToHubRouteMessage hubRouteMessage =
        DeliveryToHubRouteMessage.toHubRoute(
            delivery.getId(), delivery.getDepartureHubId(), delivery.getDestinationHubId());
    eventPublisher.sendMessagesToHubRoute(hubRouteMessage);

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

    List<UUID> routeIdList = new ArrayList<>();
    // DeliveryRoute 객체 생성 및 Delivery에 추가
    routeDtos.forEach(routeDto -> {
      DeliveryRoute route = new DeliveryRoute(delivery, routeDto);
      delivery.addRoute(route);
      route.updateCreatedBy(delivery.getCreatedBy());

      DeliveryPerson dp = deliveryPersonRepository.findBySequence(routeDto.getSequence())
          .orElseThrow(() -> new IllegalArgumentException("delivery Person not found"));

      route.updateDeliveryPerson(dp.getId());
      routeIdList.add(route.getId());

      // 로그로 모든 필드 출력
      String logMessage = String.format(
          "Created DeliveryRoute:%n" +
              "  Route ID: %s%n" +
              "  Delivery ID: %s%n" +
              "  Sequence: %d%n" +
              "  Departure Hub ID: %s%n" +
              "  Arrival Hub ID: %s%n" +
              "  Expected Distance: %d%n" +
              "  Expected Time: %d%n" +
              "  Actual Distance: %d%n" +
              "  Actual Time: %d%n" +
              "  Route Status: %s%n" +
              "  Delivery Person ID: %s",
          route.getId(), route.getDelivery().getId(), route.getSequence(),
          route.getDepartureHubId(), route.getArrivalHubId(),
          route.getExpectedDistance(), route.getExpectedTime(),
          route.getActualDistance(), route.getActualTime(),
          route.getRouteStatus().getDescription(), route.getDeliveryPersonId()
      );
      log.info(logMessage);
    });

    // Delivery 상태 변경
    if (delivery.getDeliveryStatus() == null
        || delivery.getDeliveryStatus() == DeliveryStatus.PENDING_AT_HUB) {
      delivery.updateStatus(DeliveryStatus.PENDING_AT_HUB);
    }

    deliveryRepository.save(delivery);

    // 메시지 발행
    DeliveryPerson dp = deliveryPersonRepository.findBySequence(routeDtos.get(0).getSequence())
        .orElseThrow(() -> new IllegalArgumentException("Delivery Person not found"));
    DeliveryToSlackMessage slackMessage = DeliveryToSlackMessage.toSlack(
        delivery.getId(), dp.getUsername());
    eventPublisher.sendMessagesToSlack(slackMessage);

    DeliveryToOrderMessage orderMessage =
        DeliveryToOrderMessage.toOrder(delivery.getId(), delivery.getDeliveryStatus());
    eventPublisher.sendMessagesToOrder(orderMessage);

    DeliveryToHubRouteMessage hubRouteMessage =
        DeliveryToHubRouteMessage.toHubRoute(delivery.getId(), delivery.getDepartureHubId(),
            delivery.getDestinationHubId());
    eventPublisher.sendMessagesToHubRoute(hubRouteMessage);

    return routeIdList;
  }

//  // 배송 상태 변경
//  @Transactional
//  public DeliveryResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status
//  ) {
//    Delivery delivery = deliveryRepository.findById(deliveryId)
//        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
//
//    delivery.updateStatus(status);
//    delivery.updateModificationInfo(delivery.getUpdatedBy());
//
//    return new DeliveryResponse(delivery);
//  }

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

  // 배송 상태 변경 (문자열로 받아서)
  @Transactional
  public DeliveryResponse updateDeliveryStatusByDescription(UUID deliveryId, String description
  ) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    delivery.updateStatusByDescription(description);
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

    route.updateActualTimeAndDistance(status, actualDistance, actualTime);
    route.updateCreatedBy(delivery.getCreatedBy());

    return new DeliveryRouteResponse(route);
  }

  // 배송 수정(받는 사람 or 배송 딤당자 변경)
  @Transactional
  public DeliveryResponse updateDelivery(UUID deliveryId, UpdateDeliveryRequest request) {
    Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    delivery.update(request);
    delivery.updateModificationInfo(delivery.getUpdatedBy());
    deliveryRepository.save(delivery);

    // 메시지 발행
    DeliveryPerson dp = deliveryPersonRepository.findById(request.getCompanyDeliveryPersonId())
        .orElseThrow(() -> new IllegalArgumentException("Company Delivery Person not found"));

    DeliveryToSlackMessage slackMessage = DeliveryToSlackMessage.toSlack(
        delivery.getId(), dp.getUsername());
    eventPublisher.sendMessagesToSlack(slackMessage);

    return new DeliveryResponse(delivery);
  }

  // 배송경로 수정
  @Transactional
  public void updateRoute(UUID deliveryId, UUID routeId,
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

    // 상태 전이
    switch (RouteStatus.valueOf(routeDto.getStatus())) {
      case MOVING_TO_HUB:
        delivery.updateStatus(DeliveryStatus.MOVING_TO_HUB_);
        break;
      case ARRIVED_AT_DESTINATION_HUB:
        delivery.updateStatus(DeliveryStatus.ARRIVED_AT_DESTINATION_HUB);
        break;
      case IN_DELIVERY:
        delivery.updateStatus(DeliveryStatus.IN_DELIVERY);
        break;
      default:
        // WAITING_FOR_HUB_MOVING일 경우
        delivery.updateStatus(DeliveryStatus.PENDING_AT_HUB);
        break;
    }

    String logMessage = String.format(
        "Updated DeliveryRoute:%n" +
            "  Route ID: %s%n" +
            "  Delivery ID: %s%n" +
            "  Sequence: %d%n" +
            "  Departure Hub ID: %s%n" +
            "  Arrival Hub ID: %s%n" +
            "  Expected Distance: %d%n" +
            "  Expected Time: %d%n" +
            "  Actual Distance: %d%n" +
            "  Actual Time: %d%n" +
            "  Route Status: %s%n" +
            "  Delivery Person ID: %s",
        route.getId(), route.getDelivery().getId(), route.getSequence(),
        route.getDepartureHubId(), route.getArrivalHubId(),
        route.getExpectedDistance(), route.getExpectedTime(),
        route.getActualDistance(), route.getActualTime(),
        route.getRouteStatus().getDescription(), route.getDeliveryPersonId()
    );
    log.info(logMessage);

    route.updateModificationInfo(route.getUpdatedBy());

    // 메시지 발행
    DeliveryToOrderMessage orderMessage =
        DeliveryToOrderMessage.toOrder(delivery.getId(), delivery.getDeliveryStatus());
    eventPublisher.sendMessagesToOrder(orderMessage);

    DeliveryToHubRouteMessage hubRouteMessage =
        DeliveryToHubRouteMessage.toHubRoute(delivery.getId(), delivery.getDepartureHubId(),
            delivery.getDestinationHubId());
    eventPublisher.sendMessagesToHubRoute(hubRouteMessage);
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

  // 메시지 수신하여 배송 생성
  @Transactional
  public void createDeliveryFromOrder(OrderToDeliveryMessage message) {
    log.info("Processing OrderToDeliveryMessage: orderId={}", message.getOrderId());
    // TODO : error 처리 필요
    CreateDeliveryRequest request = new CreateDeliveryRequest(
        message.getOrderId(), message.getDepartureHubId(), message.getDestinationHubId(),
        message.getDeliveryAddress(), message.getReceiver(), message.getReceiverSnsId()
    );

    Delivery delivery = new Delivery(request);
    deliveryRepository.save(delivery);
    delivery.updateCreatedBy(delivery.getCreatedBy());

    // 메시지 발행
    DeliveryToOrderMessage orderMessage = DeliveryToOrderMessage.toOrder(
        delivery.getId(), delivery.getDeliveryStatus());
    eventPublisher.sendMessagesToOrder(orderMessage);

    DeliveryToHubRouteMessage hubRouteMessage =
        DeliveryToHubRouteMessage.toHubRoute(delivery.getId(), delivery.getDepartureHubId(),
            delivery.getDestinationHubId());
    eventPublisher.sendMessagesToHubRoute(hubRouteMessage);
  }

  // 메시지 수신하여 배송경로 생성
  @Transactional
  public void handleHubRouteUpdate(HubRouteToDeliveryMessage message) {
    log.info("Processing HubRouteToDeliveryMessage: deliveryId={}", message.getDeliveryId());
    // TODO : error 처리 필요
    Delivery delivery = deliveryRepository.findById(message.getDeliveryId())
        .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

    // HubRouteToDeliveryMessage에서 경로 정보 추출
    CreateDeliveryRouteFromMessageRequest routeDto = new CreateDeliveryRouteFromMessageRequest(
        message.getSequence(),
        message.getDepartureHubId(),
        message.getArrivalHubId(),
        message.getExpectedDistance(),
        message.getExpectedTime()
    );

    // DeliveryRoute 생성
    DeliveryRoute route = new DeliveryRoute(delivery, routeDto);
    delivery.addRoute(route);
    route.updateCreatedBy(delivery.getCreatedBy());

    DeliveryPerson dp = deliveryPersonRepository.findBySequence(routeDto.getSequence())
        .orElseThrow(() -> new IllegalArgumentException("Delivery Person not found"));
    route.updateDeliveryPerson(dp.getId());

    // 로깅 (createRoute 스타일)
    String logMessage = String.format(
        "Created DeliveryRoute:%n" +
            "  Route ID: %s%n" +
            "  Delivery ID: %s%n" +
            "  Sequence: %d%n" +
            "  Departure Hub ID: %s%n" +
            "  Arrival Hub ID: %s%n" +
            "  Expected Distance: %d%n" +
            "  Expected Time: %d%n" +
            "  Actual Distance: %d%n" +
            "  Actual Time: %d%n" +
            "  Route Status: %s%n" +
            "  Delivery Person ID: %s",
        route.getId(), route.getDelivery().getId(), route.getSequence(),
        route.getDepartureHubId(), route.getArrivalHubId(),
        route.getExpectedDistance(), route.getExpectedTime(),
        route.getActualDistance(), route.getActualTime(),
        route.getRouteStatus().getDescription(), route.getDeliveryPersonId()
    );
    log.info(logMessage);

    // 상태 업데이트
    switch (message.getDescription()) {
      case "허브 이동 중":
        route.updateRouteStatus(RouteStatus.MOVING_TO_HUB);
        delivery.updateStatus(DeliveryStatus.MOVING_TO_HUB_);
        break;
      case "허브 도착":
        route.updateRouteStatus(RouteStatus.ARRIVED_AT_DESTINATION_HUB);
        delivery.updateStatus(DeliveryStatus.ARRIVED_AT_DESTINATION_HUB);
        break;
      case "배송 중":
        route.updateRouteStatus(RouteStatus.IN_DELIVERY);
        delivery.updateStatus(DeliveryStatus.IN_DELIVERY);
        break;
      case "배송 완료":
        delivery.updateStatus(DeliveryStatus.DELIVERED);
        break;
      default:
        // 기본적으로 경로 생성 시 상태 유지
        route.updateRouteStatus(RouteStatus.WAITING_FOR_HUB_MOVING);
        delivery.updateStatus(DeliveryStatus.PENDING_AT_HUB);
        break;
    }

    deliveryRepository.save(delivery);

    // 메시지 발행
    DeliveryToSlackMessage slackMessage = DeliveryToSlackMessage.toSlack(
        delivery.getId(), dp.getUsername());
    eventPublisher.sendMessagesToSlack(slackMessage);

    DeliveryToOrderMessage orderMessage = DeliveryToOrderMessage.toOrder(
        delivery.getId(), delivery.getDeliveryStatus());
    eventPublisher.sendMessagesToOrder(orderMessage);

    DeliveryToHubRouteMessage hubRouteMessage = DeliveryToHubRouteMessage.toHubRoute(
        delivery.getId(), delivery.getDepartureHubId(), delivery.getDepartureHubId());
    eventPublisher.sendMessagesToHubRoute(hubRouteMessage);

    if (delivery.getDeliveryStatus() == DeliveryStatus.DELIVERED) {
      DeliveryToOrderMessage completedMessage = DeliveryToOrderMessage.toOrder(
          delivery.getId(), delivery.getDeliveryStatus());
      eventPublisher.sendMessagesToOrder(completedMessage);
    }
  }
}
