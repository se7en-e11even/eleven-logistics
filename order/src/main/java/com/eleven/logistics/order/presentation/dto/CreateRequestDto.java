package com.eleven.logistics.order.presentation.dto;

import com.eleven.logistics.order.application.dto.CreateDto;
import com.eleven.logistics.order.application.dto.OrderProductDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateRequestDto(
        @NotNull(message = "supply_id 는 필수 항목입니다.")
        UUID supplyId,

        @NotNull(message = "receiver_id 는 필수 항목입니다.")
        UUID receiverId,

        String request,

        @Valid // list 내부 객체도 검증이 필요할 경우
        @NotNull(message = "주문 상품이 없습니다.")
        List<OrderProductCreateDto> orderProductCreateDtoList
) {

    public CreateDto toDto() {
        return CreateDto.create(
                supplyId,
                receiverId,
                request,
                orderProductCreateDtoList.stream()
                        .map(OrderProductCreateDto::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Builder
    public record OrderProductCreateDto(
            @NotNull(message = "product_id 는 필수 항목입니다.")
            UUID productId,

            @NotNull(message = "가격은 필수 항목입니다.")
                @Positive(message = "가격은 양수입니다.")
            int price,

            @NotNull(message = "수량은 필수 항목입니다.")
                @Positive(message = "수량은 양수입니다.")
            int quantity
    ) {
        public static OrderProductDto toDto(OrderProductCreateDto createDto) {
            return OrderProductDto.create(
                    null,
                    createDto.productId,
                    createDto.price,
                    createDto.quantity
            );
        }
    }
}
