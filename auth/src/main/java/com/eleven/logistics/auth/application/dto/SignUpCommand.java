package com.eleven.logistics.auth.application.dto;

import com.eleven.logistics.auth.domain.vo.Role;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCommand {
    private String username;
    private String password;
    private String slackAccount;
    private String role;

    private Role convertRole(String requestRole) {
        for (Role role : Role.values()) {
            if (Objects.equals(role.name(), requestRole)) {
                return role;
            }
        }
        throw new ValidationException("유효하지 않은 role값 입니다.");
    }

    public Role getRole() {
        return convertRole(role);
    }


}