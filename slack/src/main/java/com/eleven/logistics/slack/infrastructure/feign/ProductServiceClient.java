package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.slack.application.querydto.FindProductQuery;
import com.eleven.logistics.slack.application.querydto.ListProductQuery;
import com.eleven.logistics.slack.application.external.ProductService;
import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductServiceClient extends ProductService {

    @GetMapping("/api/products/{product_id}")
    FindProductQuery read(@PathVariable UUID product_id);


    @GetMapping("/api/products")
    ListProductQuery<FindProductQuery> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderBy
    );
}
