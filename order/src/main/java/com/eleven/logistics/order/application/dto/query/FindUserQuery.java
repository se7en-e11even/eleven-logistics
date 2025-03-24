package com.eleven.logistics.order.application.dto.query;

public record FindUserQuery(
        Long id,
        String username,
        String slackAccount,
        Role role
) {
    public enum Role {
        MASTER, HUB, COMPANY, DELIVERY
    }
}
