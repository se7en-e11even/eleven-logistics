package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.dto.query.FindProductQuery;
import com.eleven.logistics.order.application.port.out.ProductPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient extends ProductPort {

    @Override
    @GetMapping("/api/products/{product_id}")
    FindProductQuery getProduct(@PathVariable String product_id);
}
