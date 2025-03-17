package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryRepository {

  Delivery save(Delivery delivery);

  Optional<Delivery> findById(UUID deliveryId);

  @Query("SELECT d FROM Delivery d WHERE "
      + "(:orderId IS NULL OR d.orderId = :orderId) "
      + "AND (:departureHubId IS NULL OR d.departureHubId = :departureHubId) "
      + "AND (:destinationHubId IS NULL OR d.destinationHubId = :destinationHubId)"
      + "AND (:deliveryStatus IS NULL OR LOWER(d.deliveryStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))"
      + "AND (:keyword IS NULL OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :keyword, '%')))"
      + "OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Delivery> findAllByKeyword(
      UUID orderId,
      UUID departureHubId,
      UUID destinationHubId,
      String keyword,
      DeliveryStatus deliveryStatus,
      Pageable pageable);
}
