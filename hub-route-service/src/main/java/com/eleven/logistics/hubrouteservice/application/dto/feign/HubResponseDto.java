package com.eleven.logistics.hubrouteservice.application.dto.feign;

import com.eleven.logistics.hubrouteservice.application.dto.CompanyResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.PageResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubResponseDto {
    private UUID id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private PageResponseDto<CompanyResponseDto> companyList;
}
