package com.eleven.logistics.order.presentation.dto.request;

import com.eleven.logistics.order.application.dto.command.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        String request,

        @Valid // list 내부 객체도 검증이 필요할 경우
        @NotNull(message = "주문 상품이 없습니다.")
        List<CreateOrderProductRequest> requestList
) {

    public CreateOrderCommand toCommand() {
        return CreateOrderCommand.of(
                request,
                requestList.stream()
                        .map(CreateOrderProductRequest::toCommand)
                        .toList()
        );
    }

    public record CreateOrderProductRequest(
            @NotNull(message = "product_id 는 필수 항목입니다.")
            UUID productId,

            @NotNull(message = "가격은 필수 항목입니다.")
                @Positive(message = "가격은 양수입니다.")
            Integer price,

            @NotNull(message = "수량은 필수 항목입니다.")
                @Positive(message = "수량은 양수입니다.")
            Integer quantity
    ) {
        public static CreateOrderCommand.CreateOrderProductCommand toCommand(CreateOrderProductRequest request) {
            return CreateOrderCommand.CreateOrderProductCommand.of(
                    request.productId,
                    request.price,
                    request.quantity
            );
        }
    }
}
