package com.eleven.logistics.delivery.application;

import com.eleven.logistics.delivery.presentation.dtos.DeliveryMessage;

public interface DeliveryEventPublisher {

  void sendMessagesToOrder(DeliveryMessage message);

  void sendMessagesToHubRoute(DeliveryMessage message);

  void sendMessagesToSlack(DeliveryMessage message);
}
