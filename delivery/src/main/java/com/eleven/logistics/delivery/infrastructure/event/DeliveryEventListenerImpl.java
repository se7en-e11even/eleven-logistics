package com.eleven.logistics.delivery.infrastructure.event;

import com.eleven.logistics.delivery.application.dtos.event.HubRouteToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.SlackToDeliveryMessage;
import com.eleven.logistics.delivery.application.event.DeliveryEventListener;
import com.eleven.logistics.delivery.application.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventListenerImpl implements DeliveryEventListener {

  private final DeliveryService deliveryService;

  //RabbitMQ consumer
  @RabbitListener(queues = "order.delivery")
  public void receiveOrderMessage(OrderToDeliveryMessage message) {
    log.info(
        "From order: orderId={}, departureHubId={}, destinationHubId={}, deliveryAddress={}, receiver={}, receiverSnsId={}",
        message.getOrderId(), message.getDepartureHubId(), message.getDestinationHubId(),
        message.getDeliveryAddress(), message.getReceiver(), message.getReceiverSnsId());

    deliveryService.createDeliveryFromOrder(message);
  }

  @RabbitListener(queues = "hubroute.delivery")
  public void receiveHubRouteMessage(HubRouteToDeliveryMessage message) {
    log.info(
        "From hubroute: deliveryId={}, description={}",
        message.getDeliveryId(), message.getDescription());

    deliveryService.handleHubRouteUpdate(message);
  }

  @RabbitListener(queues = "delivery.err.order")
  public void receiveOrderErrorMessages(OrderToDeliveryMessage message) {
    log.error(
        "Error Message From order: orderId={}, errType={}",
        message.getOrderId(), message.getErrType());
  }

  @RabbitListener(queues = "delivery.err.slack")
  public void receiveSlackErrorMessages(SlackToDeliveryMessage message) {
    log.error(
        "Error Message From slack: orderId={}, errType={}",
        message.getOrderId(), message.getErrType());
  }

  @RabbitListener(queues = "delivery.err.hubroute")
  public void receiveHubRouteErrorMessages(HubRouteToDeliveryMessage message) {
    log.error(
        "Error Message From hubroute: deliveryId={}, errType={}",
        message.getDeliveryId(), message.getErrType());

  }
}
