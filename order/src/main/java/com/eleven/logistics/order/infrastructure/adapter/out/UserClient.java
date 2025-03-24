package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.dto.query.FindUserQuery;
import com.eleven.logistics.order.application.port.out.UserPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClient extends UserPort {

    @Override
    @GetMapping("/api/users/{username}")
    FindUserQuery getUserByUsername(@PathVariable String username);
}