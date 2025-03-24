package com.eleven.logistics.slack.infrastructure.rabbitmq;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DeliveryErrorMessage {
    private UUID deliveryId;
    private String username;
    private List<String> errorMessages;

    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }
}