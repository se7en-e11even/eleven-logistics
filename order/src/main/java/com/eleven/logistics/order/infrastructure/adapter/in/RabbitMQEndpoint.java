package com.eleven.logistics.order.infrastructure.adapter.in;

import com.eleven.logistics.order.application.dto.message.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQEndpoint {

    @RabbitListener(queues = "${message.receive.queue.order}")
    public void receive(OrderMessage message) {
        log.info("receive message: {}", message);
    }
}
