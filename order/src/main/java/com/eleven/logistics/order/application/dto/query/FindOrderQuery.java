package com.eleven.logistics.order.application.dto.query;

import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import com.eleven.logistics.order.domain.vo.FindOrder;
import lombok.AccessLevel;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record FindOrderQuery(
        UUID orderId,
        UUID supplyId,
        UUID receiverId,
        UUID deliveryId,
        String orderStatus,
        String request,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FindOrderProductQuery> orderProductQueryList
) implements Serializable {

    public static FindOrderQuery from(Order order) {
        List<FindOrderProductQuery> orderProductList = order.getOrderProductList().stream()
                .map(FindOrderProductQuery::from)
                .toList();

        return FindOrderQuery.builder()
                .orderId(order.getOrderId())
                .supplyId(order.getSupplyId())
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDeliveryId())
                .orderStatus(order.getOrderStatus().name())
                .request(order.getRequest())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderProductQueryList(orderProductList)
                .build();
    }

    public static FindOrderQuery from(FindOrder findOrder) {
        List<FindOrderProductQuery> orderProductList = findOrder.orderProductList().stream()
                .map(FindOrderProductQuery::from)
                .toList();

        return FindOrderQuery.builder()
                .orderId(findOrder.orderId())
                .supplyId(findOrder.supplyId())
                .receiverId(findOrder.receiverId())
                .deliveryId(findOrder.deliveryId())
                .orderStatus(findOrder.orderStatus())
                .request(findOrder.request())
                .createdAt(findOrder.createdAt())
                .updatedAt(findOrder.updatedAt())
                .orderProductQueryList(orderProductList)
                .build();
    }

    @Builder
    public record FindOrderProductQuery(
            UUID orderProductId,
            UUID productId,
            Integer price,
            Integer quantity
    ) implements Serializable {
        public static FindOrderProductQuery from(OrderProduct orderProduct) {
            return FindOrderProductQuery.builder()
                    .orderProductId(orderProduct.getOrderProductId())
                    .productId(orderProduct.getProductId())
                    .price(orderProduct.getPrice())
                    .quantity(orderProduct.getQuantity())
                    .build();
        }

        public static FindOrderProductQuery from(FindOrder.FindOrderProduct findOrderProduct) {
            return FindOrderProductQuery.builder()
                    .orderProductId(findOrderProduct.orderProductId())
                    .productId(findOrderProduct.productId())
                    .price(findOrderProduct.price())
                    .quantity(findOrderProduct.quantity())
                    .build();
        }
    }
}
