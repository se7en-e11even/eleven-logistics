package com.eleven.logistics.order.presentation.dto;

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
class CreateRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateRequestDto> json;

    @Test
    @DisplayName("요청 데이터 역직렬화 테스트")
    void deserialize() throws IOException {
        // given
        var content = """
                {
                    "supplyId": "a858fb2e-b6c6-41c6-8c6c-98a8cadfc9b8",
                    "receiverId": "2bf408a9-226d-4f40-b310-f2e05becf827",
                    "request": "빨리 보내주세요.",
                    "orderProductCreateDtoList": [
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
                .isEqualTo(new CreateRequestDto(
                        UUID.fromString("a858fb2e-b6c6-41c6-8c6c-98a8cadfc9b8"),
                        UUID.fromString("2bf408a9-226d-4f40-b310-f2e05becf827"),
                        "빨리 보내주세요.",
                        List.of(new CreateRequestDto.OrderProductCreateDto(
                                UUID.fromString("347eb391-73ed-4ec6-8eea-f980fdbb0b77"),
                                1000,
                                10
                        ))
                ));
    }
}