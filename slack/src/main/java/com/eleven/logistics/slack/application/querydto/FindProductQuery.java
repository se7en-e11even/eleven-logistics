package com.eleven.logistics.slack.application.querydto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FindProductQuery(
        @JsonProperty("productId") UUID productId,
        @JsonProperty("companyId") UUID companyId,
        @JsonProperty("hubId") UUID hubId,
        @JsonProperty("name") String name,
        @JsonProperty("price") Integer price,
        @JsonProperty("stockQuantity") Integer stockQuantity
) {
    // 기본값 처리
    public FindProductQuery {
        name = name != null ? name : "";
    }
}
