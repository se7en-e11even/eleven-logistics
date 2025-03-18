package com.eleven.logistics.hubrouteservice.application.service.external;

import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface HubService {
    HubResponseDto getHubById(@PathVariable UUID hubId);
}
