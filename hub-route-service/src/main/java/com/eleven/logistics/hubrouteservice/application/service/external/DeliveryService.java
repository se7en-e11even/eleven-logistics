package com.eleven.logistics.hubrouteservice.application.service.external;

import com.eleven.logistics.hubrouteservice.application.dto.feign.CreateDeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    List<UUID> createDeliveryRoute(@PathVariable UUID hubId, List<CreateDeliveryRouteRequest> deliveryRouteRequest);

    void updateDeliveryRoute(@PathVariable UUID deliveryId, @PathVariable UUID routeId, DeliveryRouteRequest updateRequest);

}
