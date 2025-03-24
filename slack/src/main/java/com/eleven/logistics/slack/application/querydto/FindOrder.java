package com.eleven.logistics.slack.application.querydto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FindOrder(
        UUID orderId,
        UUID supplyId,
        UUID receiverId,
        UUID deliveryId,
        String orderStatus,
        String request,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FindOrderProduct> orderProductList
) implements Serializable {

    public record FindOrderProduct(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {
    }
}
