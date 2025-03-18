package com.eleven.logistics.auth.presentation.rest.dto;

import com.eleven.logistics.auth.application.dto.SignInCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDto {

    private String username;
    private String password;

    // `SignInCommand`로 변환하는 메서드 추가
    public SignInCommand toCommand() {
        return new SignInCommand(username, password);
    }
}
