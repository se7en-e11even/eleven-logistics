package com.eleven.logistics.product.presentation.dto;

import com.eleven.logistics.product.application.dto.CreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateRequestDto(
        // UUID 는 NullCheck 만 가능하다. NotBlank 는 문자열 전용
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
        Integer quantity
) {
        public CreateDto toDto() {
                return CreateDto.create(
                        companyId,
                        hubId,
                        name,
                        price,
                        quantity
                );
        }
}
