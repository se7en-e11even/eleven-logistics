package com.eleven.logistics.hubrouteservice.application.dto;

import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class HubRouteResponseDto {
    private UUID id;
    private UUID originHubId;
    private UUID destinationHubId;
    private String originHubName;
    private String destinationHubName;
    private int duration;
    private int distance;

    public static HubRouteResponseDto of(HubRoute hubRoute) {
        return HubRouteResponseDto.builder()
                .id(hubRoute.getId())
                .originHubId(hubRoute.getOriginHubId())
                .destinationHubId(hubRoute.getDestinationHubId())
                .originHubName(hubRoute.getOriginHubName())
                .destinationHubName(hubRoute.getDestinationHubName())
                .duration(hubRoute.getDuration())
                .distance(hubRoute.getDistance())
                .build();
    }
}
