package com.eleven.logistics.delivery.application.service.delivery;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import com.eleven.logistics.delivery.presentation.dtos.delivery.CreateDeliveryRequest;
import com.eleven.logistics.delivery.presentation.dtos.delivery.DeliveryResponse;
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
}
