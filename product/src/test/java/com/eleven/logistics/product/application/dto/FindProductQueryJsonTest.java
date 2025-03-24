package com.eleven.logistics.product.application.dto;

import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class FindProductQueryJsonTest {

    @Autowired
    private JacksonTester<FindProductQuery> json;

    @Test
    @DisplayName("응답 객체 직렬화 테스트")
    void toJson() throws IOException {
        // given
        var product = new FindProductQuery(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "product1",
                10000,
                100,
                LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                LocalDateTime.of(2025, 3, 17, 13, 1, 2)
        );

        // when
        var jsonContent = json.write(product);

        // then
        // UUID, LocalDateTime 등의 객체는 비교 시 toString()으로 변환해 주어야 한다.
        assertThat(jsonContent).extractingJsonPathValue("@.productId")
                .isEqualTo(product.productId().toString());
        assertThat(jsonContent).extractingJsonPathValue("@.companyId")
                .isEqualTo(product.companyId().toString());
        assertThat(jsonContent).extractingJsonPathValue("@.hubId")
                .isEqualTo(product.hubId().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.name")
                .isEqualTo(product.name());
        assertThat(jsonContent).extractingJsonPathValue("@.createdAt")
                .isEqualTo(product.createdAt().toString());
        assertThat(jsonContent).extractingJsonPathValue("@.updatedAt")
                .isEqualTo(product.updatedAt().toString());
    }
}