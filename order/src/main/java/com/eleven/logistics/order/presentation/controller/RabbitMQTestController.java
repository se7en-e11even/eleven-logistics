package com.eleven.logistics.order.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbitmq")
@RequiredArgsConstructor
public class RabbitMQTestController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/send-test")
    public String sendTestMessage() {
        String message = "Eleven-Logistics!";
        try {
            rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", message);
            return "메시지 전송 성공!" + message;
        } catch (Exception e) {
            return "RabbitMQ 연결 실패: " + e.getMessage();
        }
    }
}