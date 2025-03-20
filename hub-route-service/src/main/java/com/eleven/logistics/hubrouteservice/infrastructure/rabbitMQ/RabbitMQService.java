package com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

    @Value("${message.queue.delivery}")
    private String deliveryQueue;

    private final RabbitTemplate rabbitTemplate;


    public void sendDeliveryMessage(DeliveryMessage deliveryMessage) {
        rabbitTemplate.convertSendAndReceive(deliveryQueue, deliveryMessage);
    }
}


