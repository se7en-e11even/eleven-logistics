package com.eleven.logistics.delivery.application.event;

import com.eleven.logistics.delivery.application.dtos.event.HubRouteMessage;
import com.eleven.logistics.delivery.application.dtos.event.OrderMessage;
import com.eleven.logistics.delivery.application.dtos.event.SlackMessage;

public interface DeliveryEventListener {

  void receiveOrderMessage(OrderMessage message);
  void receiveHubRouteMessage(HubRouteMessage message);

  void receiveOrderErrorMessages(OrderMessage message);
  void receiveSlackErrorMessages(SlackMessage message);
  void receiveHubRouteErrorMessages(HubRouteMessage message);
}
