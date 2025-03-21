package com.eleven.logistics.slack.application.querydto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ListProductQuery<T>(
        @JsonProperty("result") List<T> result,
        @JsonProperty("totalElements") long totalElements
) {
    public ListProductQuery {
        result = result != null ? result : List.of();
    }
}
