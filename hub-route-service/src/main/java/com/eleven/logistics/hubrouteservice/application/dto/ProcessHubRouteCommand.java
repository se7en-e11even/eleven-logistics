package com.eleven.logistics.hubrouteservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessHubRouteCommand {
    private UUID originHubId;
    private UUID destinationHubId;
}