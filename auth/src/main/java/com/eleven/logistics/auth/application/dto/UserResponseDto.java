package com.eleven.logistics.auth.application.dto;

import com.eleven.logistics.auth.domain.entity.User;
import com.eleven.logistics.auth.domain.vo.Role;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String slackAccount;
    private Role role;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .slackAccount(user.getSlackAccount())
                .role(user.getRole())
                .build();
    }

}