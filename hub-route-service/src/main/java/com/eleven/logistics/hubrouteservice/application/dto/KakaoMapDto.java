package com.eleven.logistics.hubrouteservice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoMapDto {
    @JsonProperty("distance")
    private int distance; // 전체 거리 (미터 단위)

    @JsonProperty("duration")
    private int duration; // 소요 시간 (초 단위)

    public KakaoMapDto(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }

}