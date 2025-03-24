package com.eleven.logistics.hub.application.dto.company;

import com.eleven.logistics.hub.domain.entity.CompanyType;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String name;
    private String address;
    private CompanyType type;
    private UUID hubId;
    private String username;

    public static CompanyDto create(String name, String address, CompanyType type, UUID hubId, String username) {
        return CompanyDto.builder()
                .name(name)
                .address(address)
                .type(type)
                .username(username)
                .hubId(hubId)
                .build();
    }
}