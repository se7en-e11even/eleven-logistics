package com.eleven.logistics.hubrouteservice.application.dto.feign;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRouteRequest {

    @NotNull
    private UUID deliveryId;

    private int sequence;

    @NotNull
    private UUID departureHubId;

    @NotNull
    private UUID arrivalHubId;

    @NotNull
    private int expectedDistance;

    @NotNull
    private int expectedTime;

}