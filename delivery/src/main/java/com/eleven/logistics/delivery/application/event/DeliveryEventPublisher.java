package com.eleven.logistics.delivery.application.event;

import com.eleven.logistics.delivery.application.dtos.event.DeliveryMessage;

public interface DeliveryEventPublisher {

  void sendMessagesToOrder(DeliveryMessage message);

  void sendMessagesToHubRoute(DeliveryMessage message);

  void sendMessagesToSlack(DeliveryMessage message);
}
