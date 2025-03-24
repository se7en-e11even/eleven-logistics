package com.eleven.logistics.slack.application.querydto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FindOrderQuery(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("supplyId") UUID supplyId,
        @JsonProperty("receiverId") UUID receiverId,
        @JsonProperty("deliveryId") UUID deliveryId,
        @JsonProperty("orderStatus") String orderStatus,
        @JsonProperty("request") String request,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("updatedAt") LocalDateTime updatedAt,
        @JsonProperty("orderProductQueryList") List<FindOrderProductQuery> orderProductQueryList
) implements Serializable {
    public record FindOrderProductQuery(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
    }
}
