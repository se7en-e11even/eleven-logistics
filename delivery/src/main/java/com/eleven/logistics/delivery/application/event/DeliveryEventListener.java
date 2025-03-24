package com.eleven.logistics.delivery.application.event;

import com.eleven.logistics.delivery.application.dtos.event.HubRouteToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderToDeliveryMessage;
import com.eleven.logistics.delivery.application.dtos.event.SlackToDeliveryMessage;

public interface DeliveryEventListener {

  void receiveOrderMessage(OrderToDeliveryMessage message);
  void receiveHubRouteMessage(HubRouteToDeliveryMessage message);

  void receiveOrderErrorMessages(OrderToDeliveryMessage message);
  void receiveSlackErrorMessages(SlackToDeliveryMessage message);
  void receiveHubRouteErrorMessages(HubRouteToDeliveryMessage message);
}
