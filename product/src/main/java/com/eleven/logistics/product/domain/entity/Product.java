package com.eleven.logistics.product.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID productId;

    private UUID companyId;

    private UUID hubId;

    private String name;

    private int price;

    private int stockQuantity;

    // TODO: 튜터님 깃 허브 보고 생성 메서드나 업데이트 메서드 수정할 것
    @Builder
    private Product(UUID companyId, UUID hubId, String name, int price, int stockQuantity) {
        this.companyId = companyId;
        this.hubId = hubId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void updateOf(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void decreaseQuantity(int stockQuantity) {
        this.stockQuantity -= stockQuantity;
    }

    public void increaseQuantity(int stockQuantity) {
        this.stockQuantity += stockQuantity;
    }

    public void deleteOf(String deletedBy) {
        super.deleteOf(deletedBy);
    }
}
