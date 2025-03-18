package com.eleven.logistics.slack.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {

    private List<Candidate> candidates;

    @Getter
    @Setter
    public static class Candidate {
        private Content content;
        private String finishReason;
    }
    @Getter
    @Setter
    public static class Content {
        private List<Parts> parts;
        private String role;
    }

    @Getter
    @Setter
    public static class Parts {
        private String message;
    }
}
