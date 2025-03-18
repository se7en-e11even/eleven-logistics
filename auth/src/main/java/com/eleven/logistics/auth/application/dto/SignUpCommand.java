package com.eleven.logistics.auth.application.dto;

import com.eleven.logistics.auth.domain.vo.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCommand {
    private String username;
    private String password;
    private String slackAccount;
    private Role role;
}