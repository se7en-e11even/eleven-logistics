package com.eleven.logistics.hubrouteservice.infrastructure.feign;

import com.eleven.logistics.hubrouteservice.application.dto.feign.CreateDeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteResponse;
import com.eleven.logistics.hubrouteservice.application.service.external.DeliveryService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient extends DeliveryService {
    @PostMapping("/api/deliveries/{deliveryId}/routes")
    List<UUID> createDeliveryRoute(@PathVariable UUID deliveryId, List<CreateDeliveryRouteRequest> deliveryRouteRequest);

    @PostMapping("/api/deliveries/{deliveryId}/routes/{routeId}")
    void updateDeliveryRoute(@PathVariable UUID deliveryId, @PathVariable UUID routeId, DeliveryRouteRequest updateRequest);
}

