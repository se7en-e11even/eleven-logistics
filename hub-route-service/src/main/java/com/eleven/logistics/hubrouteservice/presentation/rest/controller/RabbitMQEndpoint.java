package com.eleven.logistics.hubrouteservice.presentation.rest.controller;

import com.eleven.logistics.hubrouteservice.application.service.HubRouteService;
import com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ.MessageFromDelivery;
import com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ.MessageToDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEndpoint {

    private final HubRouteService hubRouteService;

    private final RabbitTemplate rabbitTemplate;
    @Value("${message.queue.delivery}")
    private String queueDelivery;

    @RabbitListener(queues = "${deliveryExchange.queue.hubroute}")
    public void createDeliveryRouteAndCalculateActualDistanceAndDuration(MessageFromDelivery messageFromDelivery) {
        log.info("MESSAGE RECEIVED FROM DELIVERY: {}", messageFromDelivery.toString());
        List<Map<String, UUID>> routes = hubRouteService.findOptimalRoute(messageFromDelivery.getOriginHubId(), messageFromDelivery.getDestinationHubId());
        hubRouteService.createDeliveryRoutes(routes, messageFromDelivery.getDeliveryId());
        // MessageToDelivery 객체 생성
        MessageToDelivery messageToDelivery = MessageToDelivery.builder()
                .deliveryId(messageFromDelivery.getDeliveryId())  // messageFromDelivery에서 전달된 deliveryId
                .description("배송 완료")  // "배송 완료" 메시지
                .build();
        rabbitTemplate.convertAndSend(queueDelivery, messageToDelivery);
        log.info("MESSAGE SEND TO DELIVERY: {}", messageToDelivery);
    }
}
