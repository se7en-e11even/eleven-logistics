package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.slack.application.external.ProductService;
import com.eleven.logistics.slack.application.orderProduct.FindProductQuery;
import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductServiceClient extends ProductService {

    @GetMapping("api/products/{product_id}")
    ResponseEntity<FindProductQuery> read(@PathVariable UUID product_id,
                                          @RequestHeader("X-Username") String username,
                                          @RequestHeader("X-Role") String role);
}
