package com.eleven.logistics.hub.application.dto.hub;

import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.domain.entity.hub.Hub;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // NULL 값 필드는 제거
public class HubResponseDto {
    private UUID id;
    private String name;
    private String address;
    private double longitude;
    private double latitude;
    private PageResponseDto<CompanyResponseDto> companyList;

    public static HubResponseDto of(Hub hub) {
        return HubResponseDto.builder()
                .id(hub.getId())
                .name(hub.getName())
                .address(hub.getAddress())
                .build();
    }

    public static HubResponseDto listOf(Hub hub, PageResponseDto<CompanyResponseDto> companyList) {
        return HubResponseDto.builder()
                .id(hub.getId())
                .name(hub.getName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .companyList(companyList)
                .build();
    }
}
