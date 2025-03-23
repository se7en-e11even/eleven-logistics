package com.eleven.logistics.slack.presentation.controller;

import com.eleven.logistics.slack.application.service.SlackService;
import com.eleven.logistics.slack.infrastructure.rabbitmq.DeliveryErrorMessage;
import com.eleven.logistics.slack.infrastructure.rabbitmq.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQController {

    private final SlackService slackService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.err.queue.delivery.slack}")
    private String deliveryErrorQueue;

    // delivery.slack 이라는 메시지 큐를 바라보는 메서드로
    // delivery 에서 보낸 메시지가 담겨있는 큐를 통해 메시지를 확인할 수 있다.
    // queue 의 consumer 역할이다. 실제로 consumer 가 생성이 됨.
    @RabbitListener(queues = "${message.queue.delivery.slack}")
    public void receiveMessage(DeliveryMessage message) {
        log.info("receive message : {}", message.getDeliveryId());
        log.info("receive message : {}", message.getUsername());
        try {
            // 메시징 큐에 담겨있는 deliveryId 와 담당자 이름을 전달
            slackService.sendMessageRabbitMQ(message.getUsername(), message.getDeliveryId());
        }catch (Exception e){
            DeliveryMessage errorMessage = DeliveryMessage.builder()
                    .deliveryId(message.getDeliveryId())
                    .username(message.getUsername())
                    .build();

            // 에러 사항을 메시지 큐에 담는다.
            rabbitTemplate.convertAndSend(deliveryErrorQueue, errorMessage);
        }
    }
    @RabbitListener(queues = "${message.err.queue.delivery.slack}")
    public void receiveErrorMessage(DeliveryMessage message) {
        log.info("receive error message - deliveryId: {}", message.getDeliveryId());
        log.info("receive error message - username: {}", message.getUsername());

        // DeliveryErrorMessage 빌더 초기화
        DeliveryErrorMessage errorBuilder = DeliveryErrorMessage.builder()
                .deliveryId(message.getDeliveryId())
                .username(message.getUsername())
                .build();

        // 간단한 에러 체크 예시
        if (message.getDeliveryId() == null) {
            errorBuilder.addErrorMessage("Delivery ID가 null 입니다.");
        }
        if (message.getUsername() == null || message.getUsername().isEmpty()) {
            errorBuilder.addErrorMessage("Username 이 비어 있거나 null 입니다.");
        }
        DeliveryErrorMessage errorMessage = errorBuilder;
        if (!errorMessage.getErrorMessages().isEmpty()) {
            log.info("에러 메시지 :  {}", errorMessage.getErrorMessages());
        } else {
            log.info("메시지 에러");
        }
    }
}
