package com.eleven.logistics.auth.domain.entity;

import com.eleven.logistics.auth.domain.vo.Role;
import com.eleven.logistics.auth.presentation.rest.dto.SignInRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Entity
@Table(name = "p_users")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "slackAccount")
    private String slackAccount;
    @Enumerated(EnumType.STRING)
    private Role role;


    public static User create(String username, String password, String slackAccount, Role role) {
        return User.builder()
                .username(username)
                .password(password)
                .slackAccount(slackAccount)
                .role(role)
                .build();
    }


    public User tryToSignIn(String username, String password, PasswordEncoder passwordEncoder) {
        if (matchesPassword(password, passwordEncoder) &&
                matchesUsername(username)) {
            return this;
        }
        throw new IllegalArgumentException("유효하지 않은 username 혹은 passoword");
    }

    private boolean matchesPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    private boolean matchesUsername(String username) {
        return Objects.equals(this.username, username);
    }


    public void update(String username, String password, String slackAccount, Role role) {
        if (username != null) {
            this.username = username;
        }
        if (password != null) {
            this.password = password;
        }
        if (slackAccount != null) {
            this.slackAccount = slackAccount;
        }
        if (role != null) {
            this.role = role;
        }
    }
}