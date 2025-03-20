package com.eleven.logistics.delivery.presentation.controller;

import com.eleven.logistics.delivery.application.service.DeliveryPersonService;
import com.eleven.logistics.delivery.domain.entity.DeliveryPersonType;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryPersonRequest;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryPersonResponse;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryPersonRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/deliveries/persons")
public class DeliveryPersonController {

  private final DeliveryPersonService deliveryPersonService;

  // 배송 담당자 조회
  @GetMapping("/{personId}")
  public ResponseEntity<DeliveryPersonResponse> getDeliveryPerson(
      @PathVariable UUID personId
  ) {
    DeliveryPersonResponse response = deliveryPersonService.getDeliveryPerson(personId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 담당자 검색
  @GetMapping("/search")
  public ResponseEntity<Page<DeliveryPersonResponse>> searchDeliveryPersons(
      @RequestParam(required = false) DeliveryPersonType type,
      @RequestParam(required = false) UUID hubId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<DeliveryPersonResponse> result = deliveryPersonService.searchDeliveryPersons(type, hubId, pageable);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  // 배송 담당자 생성
  @PostMapping
  public ResponseEntity<DeliveryPersonResponse> createDeliveryPerson(
      @Valid @RequestBody CreateDeliveryPersonRequest request
  ) {
    DeliveryPersonResponse response = deliveryPersonService.createDeliveryPerson(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 배송 담당자 수정
  @PatchMapping("/{personId}")
  public ResponseEntity<DeliveryPersonResponse> updateDeliveryPerson(
      @PathVariable UUID personId,
      @Valid @RequestBody UpdateDeliveryPersonRequest request
  ) {
    DeliveryPersonResponse response = deliveryPersonService.updateDeliveryPerson(personId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 배송 담당자 삭제
  @DeleteMapping("/{personId}")
  public ResponseEntity<Void> deleteDeliveryPerson(
      @PathVariable UUID personId
  ) {
    deliveryPersonService.deleteDeliveryPerson(personId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
