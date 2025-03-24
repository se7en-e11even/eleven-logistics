package com.eleven.logistics.order.presentation.dto;

import com.eleven.logistics.order.presentation.dto.request.CreateOrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateOrderRequestJsonTest {

    @Autowired
    private JacksonTester<CreateOrderRequest> json;

    @Test
    @DisplayName("요청 데이터 역직렬화 테스트")
    void deserialize() throws IOException {
        // given
        var content = """
                {
                    "request": "빨리 보내주세요.",
                    "requestList": [
                        {
                            "productId": "347eb391-73ed-4ec6-8eea-f980fdbb0b77",
                            "price": 1000,
                            "quantity": 10
                        }
                    ]
                }
                """;

        // when & then
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new CreateOrderRequest(
                        "빨리 보내주세요.",
                        List.of(new CreateOrderRequest.CreateOrderProductRequest(
                                UUID.fromString("347eb391-73ed-4ec6-8eea-f980fdbb0b77"),
                                1000,
                                10
                        ))
                ));
    }
}