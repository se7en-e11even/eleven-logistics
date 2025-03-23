package com.eleven.logistics.slack.presentation.dto;

import com.eleven.logistics.slack.application.slackdto.SlackDto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UpdateSlackMessageRequestDto {

    private String username;
    private String message;

    public SlackDto toDto() {
        return SlackDto.update(this.username, this.message);
    }
}
