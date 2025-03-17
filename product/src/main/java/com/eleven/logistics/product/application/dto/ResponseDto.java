package com.eleven.logistics.product.application.dto;

import com.eleven.logistics.product.domain.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 응용 계층의 dto 는 controller 계층에서 참조해도 되므로 controller 에 응답 객체를 따로 만들지 않고
 * 응용 계층의 dto 를 그대로 사용한다.
 */
@Builder(access = AccessLevel.PRIVATE)
public record ResponseDto(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        Integer price,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseDto of(Product product) {
        return ResponseDto.builder()
                .productId(product.getProductId())
                .companyId(product.getCompanyId())
                .hubId(product.getHubId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
