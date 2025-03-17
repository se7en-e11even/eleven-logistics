package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryRepository {

  Delivery save(Delivery delivery);

  Optional<Delivery> findById(UUID deliveryId);

  @Query("SELECT d FROM Delivery d WHERE "
      + "LOWER(d.orderId) LIKE LOWER(CONCAT('%', :keyword, '%')) "
      + "OR LOWER(d.departureHubId) LIKE LOWER(CONCAT('%', :keyword, '%'))"
      + "OR LOWER(d.destinationHubId) LIKE LOWER(CONCAT('%', :keyword, '%'))"
      + "OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))"
      + "OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :keyword, '%'))"
      + "OR LOWER(d.deliveryStatus) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Delivery> findAllByKeyword(String keyword, Pageable pageable);
}
