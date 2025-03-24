package com.eleven.logistics.order.domain.vo;

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
) {

    public record FindOrderProduct(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) {

    }
}
