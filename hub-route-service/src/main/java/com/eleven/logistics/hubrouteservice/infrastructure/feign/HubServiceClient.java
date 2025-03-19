package com.eleven.logistics.hubrouteservice.infrastructure.feign;

import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import com.eleven.logistics.hubrouteservice.application.service.external.HubService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubServiceClient extends HubService {

    @GetMapping("/api/hub/{hubId}")
    HubResponseDto getHubById(@PathVariable UUID hubId);
}
