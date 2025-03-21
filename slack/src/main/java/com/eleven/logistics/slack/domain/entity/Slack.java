package com.eleven.logistics.slack.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "p_slack")
public class Slack extends BaseSystemFieldEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreatedDate
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    public static Slack create(String username, String message) {
        return Slack.builder()
                .username(username)
                .message(message)
                .build();
    }
    public void update(String username, String message) {
        this.message = message;
        this.username = username;
    }
}
