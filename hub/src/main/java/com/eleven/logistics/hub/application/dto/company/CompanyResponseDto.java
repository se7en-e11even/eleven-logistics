package com.eleven.logistics.hub.application.dto.company;

import com.eleven.logistics.hub.domain.entity.CompanyType;
import com.eleven.logistics.hub.domain.entity.company.Company;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // NULL 값 필드는 제거
public class CompanyResponseDto {
    private UUID id;
    private String name;
    private String address;
    private CompanyType type;
    private UUID hubId;
    private String username;

    public static CompanyResponseDto of(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .type(company.getType())
                .hubId(company.getHub().getId())
                .username(company.getUsername())
                .build();
    }
}
