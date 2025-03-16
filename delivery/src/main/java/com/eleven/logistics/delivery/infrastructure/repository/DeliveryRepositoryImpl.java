package com.eleven.logistics.delivery.infrastructure.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.repository.DeliveryRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepositoryImpl extends JpaRepository<Delivery, UUID>, DeliveryRepository {

}
