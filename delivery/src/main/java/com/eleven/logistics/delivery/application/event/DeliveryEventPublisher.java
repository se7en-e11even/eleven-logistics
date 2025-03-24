package com.eleven.logistics.delivery.application.event;

import com.eleven.logistics.delivery.application.dtos.event.DeliveryToHubRouteMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToSlackMessage;
import com.eleven.logistics.delivery.application.dtos.event.DeliveryToOrderMessage;

public interface DeliveryEventPublisher {

  void sendMessagesToOrder(DeliveryToOrderMessage message);

  void sendMessagesToHubRoute(DeliveryToHubRouteMessage message);

  void sendMessagesToSlack(DeliveryToSlackMessage message);
}
