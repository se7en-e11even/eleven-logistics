package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.slack.application.deliverydto.DeliveryPersonResponse;
import com.eleven.logistics.slack.application.deliverydto.DeliveryResponse;
import com.eleven.logistics.slack.application.deliverydto.DeliveryRouteResponse;
import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.external.DeliveryService;
import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient extends DeliveryService {

    @GetMapping("/api/deliveries/search")
    PageResponseDto<DeliveryResponse> searchDeliveries();

    @GetMapping("/api/deliveries/{deliveryId}")
    DeliveryResponse getDelivery(@PathVariable UUID deliveryId);

    @GetMapping("/api/deliveries/persons/{personId}")
    DeliveryPersonResponse getDeliveryPerson(@PathVariable UUID personId);

    @GetMapping("/api/deliveries/persons/search")
    PageResponseDto<DeliveryPersonResponse> searchDeliveryPersons();

    @GetMapping("/api/deliveries/{deliveryId}/routes")
    List<DeliveryRouteResponse> getDeliveryRoutes(@PathVariable UUID deliveryId);
}
