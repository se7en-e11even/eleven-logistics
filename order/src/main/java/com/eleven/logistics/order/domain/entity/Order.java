package com.eleven.logistics.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID orderId;

    @Column(nullable = false)
    private UUID supplyId;

    @Column(nullable = false)
    private UUID receiverId;

    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private String request;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProductList;

    @Builder
    private Order(UUID supplyId, UUID receiverId, UUID deliveryId,
                  OrderStatus orderStatus, String request, List<OrderProduct> orderProductList) {
        this.supplyId = supplyId;
        this.receiverId = receiverId;
        this.deliveryId = deliveryId;
        this.orderStatus = orderStatus;
        this.request = request;
        this.orderProductList = orderProductList;
    }

    public void updateOf(UUID supplyId, UUID receiverId, UUID deliveryId, OrderStatus orderStatus, String request) {
        this.supplyId = supplyId;
        this.receiverId = receiverId;
        this.deliveryId = deliveryId;
        this.orderStatus = orderStatus;
        this.request = request;
    }

    public void updateState(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void deleteOf(String deletedBy) {
        super.deleteOf(deletedBy);
    }

    // 연관관계 편의 메소드
    public void addOrderProduct(OrderProduct orderProduct) {
        orderProductList.add(orderProduct);
        orderProduct.setOrder(this);
    }
}
