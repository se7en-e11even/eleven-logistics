package com.eleven.logistics.hub.infrastructure.feign;

import com.eleven.logistics.hub.application.dto.UserResponseDto;
import com.eleven.logistics.hub.application.external.UserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserServiceClient extends UserService {

    @GetMapping("/api/users/{username}")
    ResponseEntity<UserResponseDto> getUserByName(@PathVariable String username);
}
