package com.eleven.logistics.delivery.domain.repository;

import com.eleven.logistics.delivery.domain.entity.Delivery;
import com.eleven.logistics.delivery.domain.entity.DeliveryRoute;
import com.eleven.logistics.delivery.domain.entity.DeliveryStatus;
import com.eleven.logistics.delivery.domain.entity.RouteStatus;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;
import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository {

  Delivery save(Delivery delivery);

  @Query("SELECT d FROM Delivery d WHERE d.id = :deliveryId AND d.deletedAt IS NULL")
  Optional<Delivery> findById(UUID deliveryId);

  @Query("SELECT d FROM Delivery d WHERE (:status IS NULL OR d.deliveryStatus = :status) " +
      "AND (:orderId IS NULL OR d.orderId = :orderId) " +
      "AND d.deletedAt IS NULL")
  Page<Delivery> findByDeliveryStatusAndOrderId(DeliveryStatus status, UUID orderId, Pageable deliveryPages);

  @Query("SELECT r FROM DeliveryRoute r " +
      "JOIN r.delivery d " +
      "WHERE d.id = :deliveryId " +
      "AND (:status IS NULL OR r.routeStatus = :status) " +
      "AND r.deletedAt IS NULL")
  Page<DeliveryRoute> findRoutesByDeliveryIdAndRouteStatus(
      @Param("deliveryId") UUID deliveryId, @Param("status") RouteStatus status, Pageable routePages);
}
