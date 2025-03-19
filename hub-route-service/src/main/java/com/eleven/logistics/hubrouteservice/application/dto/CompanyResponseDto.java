package com.eleven.logistics.hubrouteservice.application.dto;

import java.util.UUID;

public class CompanyResponseDto {
    private UUID id;
    private String name;
    private String address;
    private PageResponseDto<CompanyResponseDto> companyList;
}
