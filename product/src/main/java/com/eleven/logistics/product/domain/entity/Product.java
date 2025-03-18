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
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID productId;

    private UUID companyId;

    private UUID hubId;

    private String name;

    private int price;

    private int quantity;

    @Builder
    private Product(UUID companyId, UUID hubId, String name, int price, int quantity) {
        this.companyId = companyId;
        this.hubId = hubId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void updateOf(UUID companyId, UUID hubId, String name, int price, int quantity) {
        this.companyId = companyId;
        this.hubId = hubId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void reduceQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void deleteOf(String deletedBy) {
        super.deleteOf(deletedBy);
    }
}
