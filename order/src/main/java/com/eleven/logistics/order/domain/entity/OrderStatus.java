package com.eleven.logistics.order.domain.entity;

public enum OrderStatus {
    PENDING,
    APPROVED,
    DELIVERING,
    COMPLETED,
    CANCELLED
    ;

    public static OrderStatus of(String status) {
        return valueOf(status);
    }
}
