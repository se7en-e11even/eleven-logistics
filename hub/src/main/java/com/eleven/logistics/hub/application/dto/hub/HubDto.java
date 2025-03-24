package com.eleven.logistics.hub.application.dto.hub;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 비즈니스 로직을 처리하는 서비스 레이어에서 사용하기 위한 객체
public class HubDto {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public static HubDto create(String name, String address) {
        return HubDto.builder()
                .name(name)
                .address(address)
                .build();
    }
}
