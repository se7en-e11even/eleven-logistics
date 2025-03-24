package com.eleven.logistics.product.presentation.dto;

import com.eleven.logistics.product.presentation.dto.request.CreateProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateProductRequestJsonTest {

    @Autowired
    private JacksonTester<CreateProductRequest> json;

    @Test
    @DisplayName("요청 데이터 역직렬화 테스트")
    void deserialize() throws IOException {
        // given
        var content = """
                {
                    "name": "product1",
                    "price": 1000,
                    "stockQuantity": 10
                }
                """;

        // when & then
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new CreateProductRequest(
                        "product1",
                        1000,
                        10
                ));
    }
}