package com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageFromDelivery {

    private UUID originHubId;
    private UUID destinationHubId;
    private UUID deliveryId;
}
