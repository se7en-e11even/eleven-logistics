package com.eleven.logistics.delivery.infrastructure.event;

import com.eleven.logistics.delivery.application.dtos.event.DeliveryToHubRouteMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToOrderMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToSlackMessage;
import com.eleven.logistics.delivery.application.dtos.event.HubRouteToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.SlackToDeliveryMessage;
import com.eleven.logistics.delivery.application.event.DeliveryEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventPublisherImpl implements DeliveryEventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${message.queue.order}")
  private String deliveryOrderQueue;

  @Value("${message.queue.slack}")
  private String deliverySlackQueue;

  @Value("${message.queue.hubroute}")
  private String deliveryHubRouteQueue;

  @Value("${err.queue.order}")
  private String deliveryErrOrderQueue;

  @Value("${err.queue.hubroute}")
  private String deliveryErrHubRouteQueue;

  @Value("${err.queue.slack}")
  private String deliveryErrSlackQueue;


  // 메시지 발행
  public void sendMessagesToOrder(DeliveryToOrderMessage message) {
    rabbitTemplate.convertAndSend(deliveryOrderQueue, message);

    log.info(
        "From Delivery to Order: Event published! message: deliveryId={}, status={}",
        message.getDeliveryId(), message.getStatus());
  }

  public void sendMessagesToHubRoute(DeliveryToHubRouteMessage message) {
    rabbitTemplate.convertAndSend(deliveryHubRouteQueue, message);

    log.info(
        "From Delivery to HubRoute: Event published! message: deliveryId={}, originHub={}, destinationHub={}",
        message.getDeliveryId(), message.getOriginHubId(), message.getDestinationHubId());
  }

  public void sendMessagesToSlack(DeliveryToSlackMessage message) {
    rabbitTemplate.convertAndSend(deliverySlackQueue, message);

    log.info(
        "From Delivery to Slack: Event published! message: deliveryId={}, username={}",
        message.getDeliveryId(), message.getUsername());
  }

  // Error 메시지 발행
  public void sendErrorMessagesToOrder(DeliveryToOrderMessage message) {
    rabbitTemplate.convertAndSend(deliveryErrOrderQueue, message);

    log.error(
        "Error sent to Order: delivery.err.order queue, message: deliveryId={}, errorMessage={}",
        message.getDeliveryId(), message.getErrorMessage()
    );
  }

  public void sendErrorMessagesToHubRoute(DeliveryToHubRouteMessage message) {
    rabbitTemplate.convertAndSend(deliveryErrHubRouteQueue, message);

    log.error(
        "Error sent to HubRoute: delivery.err.hubroute queue, message: deliveryId={}, errorMessage={}",
        message.getDeliveryId(), message.getErrorMessage());
  }

  public void sendErrorMessagesToSlack(DeliveryToSlackMessage message) {
    rabbitTemplate.convertAndSend(deliveryErrSlackQueue, message);

    log.error(
        "Error sent to HubRoute: delivery.err.slack queue, message: deliveryId={}, errMessage={}",
        message.getDeliveryId(), message.getErrorMessage());
  }
}
