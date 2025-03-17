package com.eleven.logistics.hubrouteservice.presentation.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteRequestDto {

    @NotBlank(message = "출발허브는 필수 입력 값입니다.")
    private UUID originHubId;
    @NotBlank(message = "도착허브는 필수 입력 값입니다.")
    private UUID destinationHubId;

}
