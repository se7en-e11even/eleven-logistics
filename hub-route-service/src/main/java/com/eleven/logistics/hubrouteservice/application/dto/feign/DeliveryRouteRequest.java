package com.eleven.logistics.hubrouteservice.application.dto.feign;

import com.eleven.logistics.hubrouteservice.application.dto.feign.vo.RouteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRequest {


    @NotNull
    private UUID departureHubId;

    @NotNull
    private UUID arrivalHubId;

    private int Distance;

    private int Time;
    String status;
}
