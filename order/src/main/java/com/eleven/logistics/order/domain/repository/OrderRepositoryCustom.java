package com.eleven.logistics.order.domain.repository;

import com.eleven.logistics.order.application.dto.command.ListOrderCommand;
import com.eleven.logistics.order.application.dto.query.FindOrderQuery;
import com.eleven.logistics.order.application.dto.query.ListOrderQuery;
import com.eleven.logistics.order.domain.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryCustom {

    Optional<Order> findById(UUID orderId);

    ListOrderQuery<FindOrderQuery> retrieve(String keyword, ListOrderCommand command);
}
