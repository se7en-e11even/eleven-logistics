package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.port.out.RabbitMQBrokerPort;
import com.eleven.logistics.order.application.dto.message.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQProducer implements RabbitMQBrokerPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.send.queue.delivery}")
    private String orderDeliveryQueue;

    @Override
    public void publishMessage(OrderMessage message) {
        log.info("publishMessage: {}", message);
        rabbitTemplate.convertAndSend(orderDeliveryQueue, message);
    }
}
