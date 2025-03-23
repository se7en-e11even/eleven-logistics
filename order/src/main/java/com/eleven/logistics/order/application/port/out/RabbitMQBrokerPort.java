package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.message.OrderMessage;

/**
 * DIP 적용을 위한 메시지브로커 인터페이스
 */
public interface RabbitMQBrokerPort {
    void publishMessage(OrderMessage message);
}
