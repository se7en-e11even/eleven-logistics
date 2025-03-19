package com.eleven.logistics.order.domain.vo;

public enum OrderStatus {
    PENDING,
    APPROVED,
    DELIVERING,
    COMPLETED,
    CANCELED
    ;

    public static OrderStatus of(String status) {
        return valueOf(status);
    }

}
