package com.eleven.logistics.slack.presentation.dto;

import com.eleven.logistics.slack.application.dto.SlackDto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateSlackMessageRequestDto {

    private String username;
    private String message;

    public SlackDto toDto() {
        return SlackDto.create(this.username, this.message);
    }
}
