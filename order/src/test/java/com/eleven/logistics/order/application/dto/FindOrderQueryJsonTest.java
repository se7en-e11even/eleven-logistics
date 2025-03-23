package com.eleven.logistics.order.application.dto;

import com.eleven.logistics.order.application.dto.query.FindOrderQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class FindOrderQueryJsonTest {

    @Autowired
    private JacksonTester<FindOrderQuery> json;

    @Test
    @DisplayName("응답 객체 직렬화 테스트")
    void toJson() throws IOException {
        // given
        var order = new FindOrderQuery(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PENDING",
                "빠른 배송 바랍니다.",
                LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                List.of(new FindOrderQuery.FindOrderProductQuery(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        10000,
                        10
                ))
        );

        // when
        var jsonContent = json.write(order);

        System.out.println(jsonContent);

        // Then
        assertThat(jsonContent).isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.orderStatus")
                .isEqualTo("PENDING");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request")
                .isEqualTo("빠른 배송 바랍니다.");
        assertThat(jsonContent).extractingJsonPathStringValue("$.createdAt")
                .isEqualTo("2025-03-17T13:01:02");
        assertThat(jsonContent).extractingJsonPathStringValue("$.updatedAt")
                .isEqualTo("2025-03-17T13:01:02");

        // 중첩 객체 검증
        assertThat(jsonContent).extractingJsonPathArrayValue("$.orderProductQueryList")
                .hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.orderProductQueryList[0].price")
                .isEqualTo(10000);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.orderProductQueryList[0].quantity")
                .isEqualTo(10);
    }
}