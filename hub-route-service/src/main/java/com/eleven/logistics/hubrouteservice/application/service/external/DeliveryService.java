package com.eleven.logistics.hubrouteservice.application.service.external;

import com.eleven.logistics.hubrouteservice.application.dto.feign.CreateDeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface DeliveryService {
    ResponseEntity<DeliveryRouteResponse> createDeliveryRoute(@PathVariable UUID hubId, CreateDeliveryRouteRequest deliveryRouteRequest);

    ResponseEntity<DeliveryRouteResponse> updateDeliveryRoute(@PathVariable UUID routeId, @PathVariable UUID deliveryId, DeliveryRouteRequest updateRequest);

}
