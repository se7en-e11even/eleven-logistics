package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;

public interface DeliveryRepository {

  Delivery save(Delivery delivery);
}
