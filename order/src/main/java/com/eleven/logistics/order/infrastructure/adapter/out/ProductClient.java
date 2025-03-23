package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.dto.command.ProductOrderCommand;
import com.eleven.logistics.order.application.dto.query.FindProductQuery;
import com.eleven.logistics.order.application.port.out.ProductPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductClient extends ProductPort {

    @Override
    @GetMapping("/api/products/{product_id}")
    FindProductQuery getProductByProductId(@PathVariable String product_id);

    @Override
    @PutMapping("/api/products/orders")
    void putProductOrder(@RequestBody ProductOrderCommand orderProduct);
}
