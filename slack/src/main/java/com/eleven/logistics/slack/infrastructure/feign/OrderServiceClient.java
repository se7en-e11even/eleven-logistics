package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.slack.application.external.OrderService;
import com.eleven.logistics.slack.application.orderProduct.FindOrderQuery;
import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderServiceClient extends OrderService {
    @GetMapping("/api/orders/{order_id}")
    ResponseEntity<FindOrderQuery> read(
            @PathVariable("order_id") UUID orderId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    );
}
