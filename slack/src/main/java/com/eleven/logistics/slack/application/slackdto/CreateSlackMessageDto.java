package com.eleven.logistics.slack.application.slackdto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CreateSlackMessageDto {

    private List<UserMessage> userMessages;

    @Getter
    @Setter
    public static class UserMessage {
        private String username;
        private String message;
    }
}
