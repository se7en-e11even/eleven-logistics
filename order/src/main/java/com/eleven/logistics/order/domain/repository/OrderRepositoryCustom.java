package com.eleven.logistics.order.domain.repository;

import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.vo.FindOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryCustom {

    Optional<Order> findById(UUID orderId);

    Page<FindOrder> retrieve(String keyword, Pageable pageable);
}
