package com.eleven.logistics.product.application.dto.query;

import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.domain.vo.FindProduct;
import lombok.AccessLevel;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 응용 계층의 dto 는 controller 계층에서 참조해도 되므로 controller 에 응답 객체를 따로 만들지 않고
 * 응용 계층의 dto 를 그대로 사용한다.
 */
@Builder(access = AccessLevel.PRIVATE)
public record FindProductQuery(
        UUID productId,
        UUID companyId,
        UUID hubId,
        String name,
        int price,
        int stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
    // record 타입은 기본적으로 Serializable 을 자동으로 구현하지만 커스텀 직렬화 로직이 필요한 경우나
    // 특정 직렬화 라이브러리가 이를 처리하지 못하는 경우가 있다. Redis 에서 필요하다..
    public static FindProductQuery from(Product product) {
        return FindProductQuery.builder()
                .productId(product.getProductId())
                .companyId(product.getCompanyId())
                .hubId(product.getHubId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static FindProductQuery from(FindProduct product) {
        return FindProductQuery.builder()
                .productId(product.productId())
                .companyId(product.companyId())
                .hubId(product.hubId())
                .name(product.name())
                .price(product.price())
                .stockQuantity(product.stockQuantity())
                .createdAt(product.createdAt())
                .updatedAt(product.updatedAt())
                .build();
    }
}
