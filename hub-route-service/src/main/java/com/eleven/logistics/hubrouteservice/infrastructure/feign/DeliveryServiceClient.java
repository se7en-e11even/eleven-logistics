package com.eleven.logistics.hubrouteservice.infrastructure.feign;

import com.eleven.logistics.hubrouteservice.application.dto.feign.CreateDeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteResponse;
import com.eleven.logistics.hubrouteservice.application.service.external.DeliveryService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient extends DeliveryService {
    @PostMapping("/api/deliveries/{deliveryId}/routes")
    ResponseEntity<DeliveryRouteResponse> createDeliveryRoute(@PathVariable UUID deliveryId, CreateDeliveryRouteRequest deliveryRouteRequest);

    @PatchMapping("/api/deliveries/{deliveryId}/routes/{routeId}")
    ResponseEntity<DeliveryRouteResponse> updateDeliveryRoute(@PathVariable UUID routeId, @PathVariable UUID deliveryId, DeliveryRouteRequest updateRequest);
}
