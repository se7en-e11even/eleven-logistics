package com.eleven.logistics.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private OrderProduct(Order order, UUID productId, int price, int quantity) {
        this.order = order;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }
}
