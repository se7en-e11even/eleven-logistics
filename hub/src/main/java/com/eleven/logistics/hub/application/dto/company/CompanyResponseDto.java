package com.eleven.logistics.hub.application.dto.company;

import com.eleven.logistics.hub.domain.entity.company.Company;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // NULL 값 필드는 제거
public class CompanyResponseDto {
    private String name;
    private String address;
    private Company.CompanyType type;
    private UUID hubId;
//    private UUID userId;

    public static CompanyResponseDto of(Company company) {
        return CompanyResponseDto.builder()
                .name(company.getName())
                .address(company.getAddress())
                .type(company.getType())
                .hubId(company.getHub().getId())
                .build();
    }
}
