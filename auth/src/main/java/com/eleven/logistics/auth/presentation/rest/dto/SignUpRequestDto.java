package com.eleven.logistics.auth.presentation.rest.dto;


import com.eleven.logistics.auth.domain.vo.Role;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "Username은 필수 입력 값입니다.")
    @Size(min = 4, max = 10, message = "Username은 최소 4자 이상, 10자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "Username은 알파벳 소문자(a~z)와 숫자(0~9)로만 구성되어야 합니다.")
    private String username;

    @NotBlank(message = "Password는 필수 입력 값입니다.")
    @Size(min = 8, max = 15, message = "Password는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).+$",
            message = "Password는 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "SlackId는 필수 입력 값입니다.")
    private String slackAccount;

    @NotBlank(message = "role은 필수 입력 값입니다.")
    private String role;



    private Role convertRole(String requestRole) {
        for (Role role : Role.values()) {
            if (Objects.equals(role.name(), requestRole)) {
                return role;
            }
        }
        throw new ValidationException("유효하지 않은 role값 입니다.");
    }

    // 외부에서 호출할 필요가 없다면 private으로 사용
    public Role getRole() {
        return convertRole(this.role);
    }
}