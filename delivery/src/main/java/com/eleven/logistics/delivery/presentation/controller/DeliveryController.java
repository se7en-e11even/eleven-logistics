package com.eleven.logistics.delivery.presentation.controller;

import com.eleven.logistics.delivery.application.service.delivery.DeliveryService;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliverySearchResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

  private final DeliveryService deliveryService;
  private final HttpServletRequest httpServletRequest;

  @GetMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> getDelivery(
      @RequestHeader HttpServletRequest httpServletRequest,
      @PathVariable UUID deliveryId
  ) {
    String username = httpServletRequest.getHeader("X-Username");
    String userRole = httpServletRequest.getHeader("X-Role");

    DeliveryResponse response = deliveryService.getDelivery(username, userRole, deliveryId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<DeliverySearchResponse>> getDeliveries(
      @RequestHeader HttpServletRequest httpServletRequest,
      @RequestParam(required = false) UUID orderId,
      @RequestParam(required = false) UUID departureHubId,
      @RequestParam(required = false) UUID destinationHubId,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) DeliveryStatus deliveryStatus,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "sortedBy", defaultValue = "createdAt") String sortedBy,
      @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
  ) {
    String username = httpServletRequest.getHeader("X-Username");
    String userRole = httpServletRequest.getHeader("X-Role");

    Page<DeliverySearchResponse> deliveries = deliveryService.getDeliveriesByKeyword(
        username, userRole,
        orderId, departureHubId, destinationHubId, keyword, deliveryStatus, page, size, sortedBy,
        direction);
    return ResponseEntity.status(HttpStatus.OK).body(deliveries);
  }

  @PostMapping
  public ResponseEntity<DeliveryResponse> createDelivery(
      @RequestHeader HttpServletRequest httpServletRequest,
      @Valid @RequestBody CreateDeliveryRequest request
  ) {
    String username = httpServletRequest.getHeader("X-Username");
    String userRole = httpServletRequest.getHeader("X-Role");

    DeliveryResponse response = deliveryService.createDelivery(username, userRole, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> updateDelivery(
      @RequestHeader HttpServletRequest httpServletRequest,
      @PathVariable UUID deliveryId,
      @Valid @RequestBody UpdateDeliveryRequest request
  ) {
    String username = httpServletRequest.getHeader("X-Username");
    String userRole = httpServletRequest.getHeader("X-Role");

    DeliveryResponse response = deliveryService.updateDelivery(username, userRole, deliveryId,
        request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{deliveryId}")
  public ResponseEntity<?> deleteDelivery(
      @RequestHeader HttpServletRequest httpServletRequest,
      @PathVariable UUID deliveryId
  ) {
    String username = httpServletRequest.getHeader("X-Username");
    String userRole = httpServletRequest.getHeader("X-Role");

    deliveryService.deleteDelivery(username, userRole, deliveryId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
