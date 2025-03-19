package com.eleven.logistics.hubrouteservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_hub_routes")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class HubRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID originHubId;
    private UUID destinationHubId;
    private String originHubName;
    private String destinationHubName;
    private int duration;
    private int distance;

    public static HubRoute create(UUID originHubId, UUID destinationHubId, String originHubName,String destinationHubName,int duration, int distance) {
        return HubRoute.builder()
                .originHubId(originHubId)
                .destinationHubId(destinationHubId)
                .originHubName(originHubName)
                .destinationHubName(destinationHubName)
                .duration(duration)
                .distance(distance)
                .build();
    }
}
