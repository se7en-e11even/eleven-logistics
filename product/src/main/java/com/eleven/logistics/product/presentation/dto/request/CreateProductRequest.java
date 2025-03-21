package com.eleven.logistics.product.presentation.dto.request;

import com.eleven.logistics.product.application.dto.command.CreateProductCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
        @NotBlank(message = "상품 이름은 필수 항목입니다.")
        String name,

        @NotNull(message = "가격은 필수 항목입니다.")
                @Positive(message = "가격은 양수입니다.")
        Integer price,

        @NotNull(message = "재고는 필수 항목입니다.")
                @Positive(message = "수량은 양수입니다.")
        Integer stockQuantity
) {
        public CreateProductCommand toCommand() {
                return CreateProductCommand.create(
                        name,
                        price,
                        stockQuantity
                );
        }
}
