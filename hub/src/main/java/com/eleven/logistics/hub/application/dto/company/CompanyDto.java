package com.eleven.logistics.hub.application.dto.company;

import com.eleven.logistics.hub.domain.entity.company.Company;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
// 비즈니스 로직을 처리하는 서비스 레이어에서 사용하기 위한 객체
public class CompanyDto {

    private String name;
    private String address;
    private Company.CompanyType type;
    private UUID hubId;
//    private UUID userId;

    public static CompanyDto create(String name, String address, Company.CompanyType type, UUID hubId) {
        return CompanyDto.builder()
                .name(name)
                .address(address)
                .type(type)
                .hubId(hubId)
                .build();
    }
}
