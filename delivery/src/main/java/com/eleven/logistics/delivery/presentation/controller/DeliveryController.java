package com.eleven.logistics.delivery.presentation.controller;

import com.eleven.logistics.delivery.application.dtos.DeliveryResponse;
import com.eleven.logistics.delivery.application.service.delivery.DeliveryService;
import com.eleven.logistics.delivery.application.dtos.DeliveryRouteResponse;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryRouteRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {

  private final DeliveryService deliveryService;

  // 배송 조회
  @GetMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable UUID deliveryId
  ) {
    DeliveryResponse response = deliveryService.getDelivery(deliveryId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 검색
  @GetMapping("/search")
  public ResponseEntity<Page<DeliveryResponse>> searchDeliveries(
      @RequestParam(required = false) DeliveryStatus status,
      @RequestParam(required = false) UUID orderId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<DeliveryResponse> result = deliveryService.searchDeliveries(status, orderId, pageable);
    return ResponseEntity.ok(result);
  }

  // 배송 경로 조회(특정 배송의 모든 경로)
  @GetMapping("/{deliveryId}/routes")
  public ResponseEntity<List<DeliveryRouteResponse>> getDeliveryRoutes(@PathVariable UUID deliveryId
  ) {
    List<DeliveryRouteResponse> response = deliveryService.getDeliveryRoutes(deliveryId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 경로 검색
  @GetMapping("/{deliveryId}/routes/search")
  public ResponseEntity<Page<DeliveryRouteResponse>> searchDeliveryRoutes(
      @PathVariable UUID deliveryId,
      @RequestParam(required = false) RouteStatus status,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<DeliveryRouteResponse> result = deliveryService.searchDeliveryRoutes(deliveryId, status, pageable);
    return ResponseEntity.ok(result);
  }

  // 배송 및 배송경로 생성
  @PostMapping
  public ResponseEntity<DeliveryResponse> createDelivery(
      @RequestBody CreateDeliveryRequest requestDto
  ) {
    DeliveryResponse response = deliveryService.createDelivery(
        requestDto.toDeliveryRequestDto(),
        requestDto.getRouteDtos()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 배송 상태 변경
  @PatchMapping("/{deliveryId}/status")
  public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
      @PathVariable UUID deliveryId,
      @RequestParam DeliveryStatus status
  ) {
    DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 수정
  @PatchMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> updateDelivery(
      @PathVariable UUID deliveryId,
      @Valid @RequestBody UpdateDeliveryRequest request
  ) {
    DeliveryResponse response = deliveryService.updateDelivery(deliveryId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 삭제
  @DeleteMapping("/{deliveryId}")
  public ResponseEntity<Void> deleteDelivery(
      @PathVariable UUID deliveryId
  ) {
    deliveryService.deleteDelivery(deliveryId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // 배송경로 상태 변경
  @PatchMapping("/{deliveryId}/routes/{routeId}/status")
  public ResponseEntity<DeliveryRouteResponse> updateRouteStatus(
      @PathVariable UUID deliveryId,
      @PathVariable UUID routeId,
      @RequestParam RouteStatus status,
      @RequestParam int actualDistance,
      @RequestParam int actualTime
  ) {
    DeliveryRouteResponse response = deliveryService.updateRouteStatus(deliveryId, routeId,
        status, actualDistance, actualTime);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송경로 수정
  @PatchMapping("/{deliveryId}/routes/{routeId}")
  public ResponseEntity<DeliveryRouteResponse> updateRoute(
      @PathVariable UUID deliveryId,
      @PathVariable UUID routeId,
      @RequestBody DeliveryRouteRequest routeDto
  ) {
    DeliveryRouteResponse response = deliveryService.updateRoute(deliveryId, routeId, routeDto);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송경로 삭제
  @DeleteMapping("/{deliveryId}/routes/{routeId}")
  public ResponseEntity<Void> deleteRoute(
      @PathVariable UUID deliveryId,
      @PathVariable UUID routeId
  ) {
    deliveryService.deleteRoute(deliveryId, routeId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
