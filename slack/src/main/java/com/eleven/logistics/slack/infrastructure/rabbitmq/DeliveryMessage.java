package com.eleven.logistics.slack.infrastructure.rabbitmq;

import lombok.*;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMessage {

    private UUID deliveryId;
    private String username;
}
