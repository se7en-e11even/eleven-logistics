package com.eleven.logistics.product.presentation.dto.request;

import com.eleven.logistics.product.application.dto.command.UpdateProductCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UpdateProductRequest(
        @NotNull(message = "company_id 는 필수 항목입니다.")
        UUID companyId,

        @NotNull(message = "hub_id 는 필수 항목입니다.")
        UUID hubId,

        @NotBlank(message = "상품 이름은 필수 항목입니다.")
        String name,

        @NotNull(message = "가격은 필수 항목입니다.")
            @Positive(message = "가격은 양수입니다.")
        Integer price,

        @NotNull(message = "재고는 필수 항목입니다.")
            @Positive(message = "수량은 양수입니다.")
        Integer stockQuantity
) {
    public UpdateProductCommand toCommandWithId(UUID productId) {
        return UpdateProductCommand.create(
                productId,
                this.companyId,
                this.hubId,
                this.name,
                this.price,
                this.stockQuantity
        );
    }
}
