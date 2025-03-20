package com.eleven.logistics.hubrouteservice.application.dto.feign;

import com.eleven.logistics.hubrouteservice.application.dto.feign.vo.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteResponse {
    private UUID id;
    private UUID deliveryId;
    private UUID deliveryPersonId;
    private int sequence;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private int expectedDistance;
    private int expectedTime;
    private int actualDistance;
    private int actualTime;
    private RouteStatus routeStatus;
}
