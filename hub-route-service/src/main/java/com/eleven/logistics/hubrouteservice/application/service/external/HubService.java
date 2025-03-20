package com.eleven.logistics.hubrouteservice.application.service.external;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.feign.HubResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface HubService {
    ResponseEntity<ApiResponseDto<HubResponseDto>> getHubById(@PathVariable UUID hubId);
}
