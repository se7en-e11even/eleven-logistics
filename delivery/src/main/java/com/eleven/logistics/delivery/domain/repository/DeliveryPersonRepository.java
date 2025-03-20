package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.DeliveryPerson;
import com.eleven.logistics.delivery.domain.entity.DeliveryPersonType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryPersonRepository {

  DeliveryPerson save(DeliveryPerson deliveryPerson);

  @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.id = :personId AND dp.deletedAt IS NULL")
  Optional<DeliveryPerson> findById(UUID personId);

  @Query("SELECT dp FROM DeliveryPerson dp " +
      "WHERE (:type IS NULL OR dp.deliveryPersonType = :type) " +
      "AND (:hubId IS NULL OR dp.hubId = :hubId) " +
      "AND dp.deletedAt IS NULL")
  Page<DeliveryPerson> findByDeliveryPersonTypeAndHubId(
      @Param("type") DeliveryPersonType type,
      @Param("hubId") UUID hubId, Pageable pageable);
}
