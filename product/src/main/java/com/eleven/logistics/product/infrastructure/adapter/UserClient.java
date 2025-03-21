package com.eleven.logistics.product.infrastructure.adapter;

import com.eleven.logistics.product.application.dto.query.FindUserQuery;
import com.eleven.logistics.product.application.port.out.UserPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClient extends UserPort {

    @Override
    @GetMapping("/api/users/{username}")
    FindUserQuery getUserByUsername(@PathVariable String username);
}
