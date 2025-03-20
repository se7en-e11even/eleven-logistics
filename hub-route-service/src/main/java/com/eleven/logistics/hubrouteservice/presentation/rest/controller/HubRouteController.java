package com.eleven.logistics.hubrouteservice.presentation.rest.controller;

import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.service.HubRouteService;
import com.eleven.logistics.hubrouteservice.application.service.external.DeliveryService;
import com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ.DeliveryMessage;
import com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ.RabbitMQService;
import com.eleven.logistics.hubrouteservice.presentation.rest.dto.HubRouteRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.channels.Channel;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hub-routes")
@Slf4j
public class HubRouteController {
    private final HubRouteService hubRouteService;
    private final RabbitMQService rabbitMQService;

    public HubRouteController(HubRouteService hubRouteService, RabbitMQService rabbitMQService) {
        this.hubRouteService = hubRouteService;
        this.rabbitMQService = rabbitMQService;
    }

    @PostMapping
    public ResponseEntity<HubRouteResponseDto> createHubRoute(@RequestBody HubRouteRequestDto requestDto) {
        return ResponseEntity.ok(hubRouteService.createHubRoute(requestDto.toCommand()));
    }

    /**
     * RabbitMQ에서 메시지를 받는 메서드
     *
     * @param deliveryMessage RabbitMQ 메시지
     */
    @RabbitListener(queues = "delivery.hub-route")
    // 출발 허브 → 도착 허브까지 최적 경로 찾기
    @GetMapping
    public ResponseEntity<String> findOptimalRoute(DeliveryMessage deliveryMessage) {

        // deliveryMessage에서 필요한 값 추출
        UUID originHubId = deliveryMessage.getOriginHubId();
        UUID destinationHubId = deliveryMessage.getDestinationHubId();
        UUID deliveryId = deliveryMessage.getDeliveryId();

        List<Map<UUID, UUID>> optimalRoute = hubRouteService.findOptimalRoute(originHubId, destinationHubId);
        hubRouteService.createDeliveryRoutes(optimalRoute, deliveryId);

        // delivery 시스템에 결과를 전송하기 위해 메시지 전송
        DeliveryMessage responseMessage = new DeliveryMessage(originHubId, destinationHubId, deliveryId);
        rabbitMQService.sendDeliveryMessage(responseMessage);

        return ResponseEntity.ok("허브간 배송 완료되었습니다.");
    }

    @RabbitListener(queues = "${message.err.queue.hub-route}")
    public void errHubRoute(DeliveryMessage deliveryMessage) {
        log.info("ERROR RECEIVE!! message.err.queue.hub-route");
        rollbackHubRoute(deliveryMessage);
    }

    public void rollbackHubRoute(DeliveryMessage deliveryMessage) {
        log.info("ERROR ROLLBACK!! message.err.queue.hub-route");
    }

}
