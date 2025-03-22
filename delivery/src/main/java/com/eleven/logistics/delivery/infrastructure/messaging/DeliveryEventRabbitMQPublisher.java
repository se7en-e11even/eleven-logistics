package com.eleven.logistics.delivery.infrastructure.messaging;

import com.eleven.logistics.delivery.application.DeliveryEventPublisher;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventRabbitMQPublisher implements DeliveryEventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${message.queue.order}")
  private String deliveryOrderQueue;

  @Value("${message.queue.slack}")
  private String deliverySlackQueue;

  @Value("${message.queue.hubroute}")
  private String deliveryHubRouteQueue;

  // RabbitMQ publish
  public void sendMessagesToOrder(DeliveryMessage message) {
    rabbitTemplate.convertAndSend(deliveryOrderQueue, message);
    log.info("From Delivery to Order: Event published! message: {}", message.getDeliveryId());
  }

  public void sendMessagesToHubRoute(DeliveryMessage message) {
    rabbitTemplate.convertAndSend(deliveryHubRouteQueue, message);
    log.info("From Delivery to HubRoute: Event published! message: {}", message.getDeliveryId());
  }

  public void sendMessagesToSlack(DeliveryMessage message) {
    rabbitTemplate.convertAndSend(deliverySlackQueue, message);
    log.info("From Delivery to Slack: Event published! message: {}", message.getDeliveryId());
  }
}
