package com.eleven.logistics.slack.application.slackdto;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SlackDto {

    private String username;
    private String message;

    public static SlackDto create(String username) {
        return SlackDto.builder()
                .username(username)
                .build();
    }

    public static SlackDto update(String username, String message) {
        return SlackDto.builder()
                .username(username)
                .message(message)
                .build();
    }
}
