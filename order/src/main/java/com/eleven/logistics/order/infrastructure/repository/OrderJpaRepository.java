package com.eleven.logistics.order.infrastructure.repository;

import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID>, OrderRepository {
}

