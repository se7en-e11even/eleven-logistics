package com.eleven.logistics.hub.application.dto;

import com.eleven.logistics.hub.domain.entity.Role;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private String username;
    private Role role;
}