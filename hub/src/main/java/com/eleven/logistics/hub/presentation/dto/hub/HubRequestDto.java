package com.eleven.logistics.hub.presentation.dto.hub;

import com.eleven.logistics.hub.application.dto.hub.HubDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 사용자의 입력을 받아 해당 데이터를 처리하고 서비스 레이어로 전달하는 역할
public class HubRequestDto {

    private String name;
    private String address;

    public HubDto toDto() {
        return HubDto.create(this.name, this.address);
    }
}
