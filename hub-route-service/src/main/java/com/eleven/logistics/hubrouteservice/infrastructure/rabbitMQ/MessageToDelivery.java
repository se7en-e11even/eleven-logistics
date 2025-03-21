package com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageToDelivery {
    private UUID deliveryId;
    private String description;

}
