package com.eleven.logistics.delivery.application.service.delivery;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryResponse;
import com.eleven.logistics.delivery.presentation.dtos.delivery.UpdateDeliveryRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;

  @Transactional
  public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
    Delivery delivery = deliveryRepository.save(new Delivery(request));

    return new DeliveryResponse(delivery);
  }

  @Transactional
  public DeliveryResponse updateDelivery(UUID deliveryId, UpdateDeliveryRequest request) {
    // 사용자 권한 체크

    // delivery 체크
    Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
        new IllegalArgumentException("Delivery not found"));

    // delivery 권한 체크

    // delivery 업데이트
    delivery.update(request);
    deliveryRepository.save(delivery);

    return new DeliveryResponse(delivery);
  }
}
