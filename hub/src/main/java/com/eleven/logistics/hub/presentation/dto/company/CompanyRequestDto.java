package com.eleven.logistics.hub.presentation.dto.company;

import com.eleven.logistics.hub.application.dto.company.CompanyDto;
import com.eleven.logistics.hub.domain.entity.company.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
// 사용자의 입력을 받아 해당 데이터를 처리하고 서비스 레이어로 전달하는 역할
public class CompanyRequestDto {

    private String name;
    private String address;
    private Company.CompanyType type;
    private UUID hubId;
//    private UUID userId;

    public CompanyDto toDto() {return CompanyDto.create(this.name, this.address, this.type, this.hubId);}
}
