package com.eleven.logistics.delivery.infrastructure.event;

import com.eleven.logistics.delivery.application.dtos.event.HubRouteMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderMessage;
import com.eleven.logistics.delivery.application.dtos.event.SlackMessage;
import com.eleven.logistics.delivery.application.event.DeliveryEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventListenerImpl implements DeliveryEventListener {

  //RabbitMQ consumer
  @RabbitListener(queues = "order.delivery")
  public void receiveOrderMessage(OrderMessage message) {
    log.info(
        "From order: orderId={}, departureHubId={}, destinationHubId={}, deliveryAddress={}, receiver={}, receiverSnsId={}",
        message.orderId(), message.departureHubId(), message.destinationHubId(),
        message.deliveryAddress(), message.receiver(), message.receiverSnsId());
  }

  @RabbitListener(queues = "hubroute.delivery")
  public void receiveHubRouteMessage(HubRouteMessage message) {
    log.info(
        "From hubroute: deliveryId={}, description={}",
        message.getDeliveryId(), message.getDescription());
  }

  @RabbitListener(queues = "delivery.err.order")
  public void receiveOrderErrorMessages(OrderMessage message) {
    log.error(
        "Error Message From order: orderId={}, errType={}",
        message.orderId(), message.errType());
  }

  @RabbitListener(queues = "delivery.err.slack")
  public void receiveSlackErrorMessages(SlackMessage message) {
    log.error(
        "Error Message From slack: orderId={}, errType={}",
        message.getOrderId(), message.getErrType());
  }

  @RabbitListener(queues = "delivery.err.hubroute")
  public void receiveHubRouteErrorMessages(HubRouteMessage message) {
    log.error(
        "Error Message From hubroute: deliveryId={}, errType={}",
        message.getDeliveryId(), message.getErrType());

  }
}
