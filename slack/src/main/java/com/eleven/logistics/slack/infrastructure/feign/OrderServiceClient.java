package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.slack.application.querydto.FindOrderQuery;
import com.eleven.logistics.slack.application.querydto.ListOrderQuery;
import com.eleven.logistics.slack.application.external.OrderService;

import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderServiceClient extends OrderService {

    @GetMapping(value = "/api/orders")
    ListOrderQuery<FindOrderQuery> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderBy
    );

    @GetMapping(value = "/api/orders/{orderId}")
    ResponseEntity<FindOrderQuery> read(@PathVariable("orderId") UUID orderId);
}
