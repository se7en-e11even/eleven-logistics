package com.eleven.logistics.hubrouteservice.presentation.rest.controller;

import com.eleven.logistics.hubrouteservice.application.service.HubRouteService;
import com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEndpoint {

    private final HubRouteService hubRouteService;

    @RabbitListener(queues = "${message.queue.hubroute}")
    public void createDeliveryRouteAndCalculateActualDistanceAndDuration(DeliveryMessage deliveryMessage) {
        log.info("MESSAGE RECEIVED: {}", deliveryMessage.toString());
        List<Map<String, UUID>> routes = hubRouteService.findOptimalRoute(deliveryMessage.getOriginHubId(), deliveryMessage.getDestinationHubId());
        hubRouteService.createDeliveryRoutes(routes, deliveryMessage.getDeliveryId());
    }
}
