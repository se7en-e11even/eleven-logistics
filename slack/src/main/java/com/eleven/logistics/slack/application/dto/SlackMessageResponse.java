package com.eleven.logistics.slack.application.dto;

import com.eleven.logistics.slack.domain.entity.Slack;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class SlackMessageResponse {

    private UUID id;
    private String username;
    private String message;

    public static SlackMessageResponse of(Slack slack) {
        return SlackMessageResponse.builder()
                .id(slack.getId())
                .username(slack.getUsername())
                .message(slack.getMessage())
                .build();
    }
}
