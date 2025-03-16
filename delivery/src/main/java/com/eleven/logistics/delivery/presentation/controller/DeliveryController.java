package com.eleven.logistics.delivery.presentation.controller;

import com.eleven.logistics.delivery.application.service.delivery.DeliveryService;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

  private final DeliveryService deliveryService;

  @GetMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> getDelivery(
      @PathVariable UUID deliveryId
  ) {
    DeliveryResponse response = deliveryService.getDelivery(deliveryId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping
  public ResponseEntity<DeliveryResponse> createDelivery(
      @Valid @RequestBody CreateDeliveryRequest request
  ) {
    DeliveryResponse response = deliveryService.createDelivery(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{deliveryId}")
  public ResponseEntity<DeliveryResponse> updateDelivery(
      @PathVariable UUID deliveryId,
      @Valid @RequestBody UpdateDeliveryRequest request
  ) {
    DeliveryResponse response = deliveryService.updateDelivery(deliveryId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{deliveryId}")
  public ResponseEntity<?> deleteDelivery(
      @PathVariable UUID deliveryId
  ) {
    deliveryService.deleteDelivery(deliveryId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
