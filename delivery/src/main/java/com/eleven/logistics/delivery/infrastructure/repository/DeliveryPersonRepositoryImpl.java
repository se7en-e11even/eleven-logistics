package com.eleven.logistics.delivery.infrastructure.repository;

import com.eleven.logistics.delivery.domain.entity.DeliveryPerson;
import com.eleven.logistics.delivery.domain.repository.DeliveryPersonRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonRepositoryImpl extends JpaRepository<DeliveryPerson, UUID>,
    DeliveryPersonRepository {

}
