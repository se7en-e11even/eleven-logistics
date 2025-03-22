package com.eleven.logistics.delivery.infrastructure.messaging;

import com.eleven.logistics.delivery.presentation.dtos.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventRabbitMQListener {

  //RabbitMQ consumer
  @RabbitListener(queues = "order.delivery")
  public void receiveOrderMessage(DeliveryMessage message) {
    log.info("From order: {}", message.getDeliveryId());
  }

  @RabbitListener(queues = "hubroute.delivery")
  public void receiveHubRouteMessage(DeliveryMessage message) {
    log.info("From hubroute: {}", message.getDeliveryId());
  }

  @RabbitListener(queues = "delivery.err.order")
  public void receiveOrderErrorMessages(DeliveryMessage message) {
    log.error("Error Message From order: deliveryId={}", message.getDeliveryId());
  }

  @RabbitListener(queues = "delivery.err.slack")
  public void receiveSlackErrorMessages(DeliveryMessage message) {
    log.error("Error Message From slack: deliveryId={}", message.getDeliveryId());
  }

  @RabbitListener(queues = "delivery.err.hubroute")
  public void receiveHubRouteErrorMessages(DeliveryMessage message) {
    log.error("Error Message From hubroute: deliveryId={}", message.getDeliveryId());
  }
}
