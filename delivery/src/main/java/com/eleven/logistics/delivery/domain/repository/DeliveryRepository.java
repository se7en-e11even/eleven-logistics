package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

  Delivery save(Delivery delivery);

  Optional<Delivery> findById(UUID deliveryId);
}
