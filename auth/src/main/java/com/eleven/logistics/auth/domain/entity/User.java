package com.eleven.logistics.auth.domain.entity;

import com.eleven.logistics.auth.domain.vo.Role;
import jakarta.persistence.*;
import lombok.*;

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