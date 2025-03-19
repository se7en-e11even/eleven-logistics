package com.eleven.logistics.order.presentation;

import com.eleven.logistics.order.application.OrderService;
import com.eleven.logistics.order.application.dto.CreateDto;
import com.eleven.logistics.order.application.dto.OrderProductDto;
import com.eleven.logistics.order.application.dto.ResponseDto;
import com.eleven.logistics.order.common.exception.CustomException;
import com.eleven.logistics.order.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.order.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.order.presentation.dto.CreateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.eleven.logistics.order.domain.exception.OrderErrorCode.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(OrderController.class)
class OrderControllerMvcTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private ResponseDto[] dtos;

    UUID[] uuids;

    @Test
    @DisplayName("주문 생성 요청 성공 테스트")
    void createOrder() throws JsonProcessingException {
        UUID orderId = UUID.randomUUID();
        var createRequestOrder = new CreateRequestDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "",
                List.of(new CreateRequestDto.OrderProductCreateDto(
                        UUID.randomUUID(),
                        1000,
                        1)
                )
        );

        var createRequest = objectMapper.writeValueAsString(createRequestOrder);

        given(orderService.createOrder(any(CreateDto.class)))
                .willReturn(orderId);

        // when & then
        assertThat(mvc.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest)
        ).hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", String.format("/api/orders/%s", orderId.toString()));
    }

    @Test
    @DisplayName("요청한 주문이 없으면 404")
    void readOrder_ById_NotFound() {
        UUID orderId = UUID.randomUUID();
        given(orderService.readOrder(orderId))
                .willThrow(new CustomException(ORDER_NOT_FOUND));

        // when & then
        assertThat(mvc.get().uri("/api/orders/" + orderId)
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("주문 상세 조회")
    void readOrderById() {
        given(orderService.readOrder(uuids[0]))
                .willReturn(dtos[0]);

        // when & then
        assertThat(mvc.get().uri("/api/orders/" + uuids[0])
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk();
    }

    @Test
    @DisplayName("주문 목록 조회")
    void retreiveOrders() throws JsonProcessingException {
        var pageRequestDto = PageRequestDto.of(0, 10);
        var pageResponseDto = new PageResponseDto<>(List.of(dtos), 3L);
        given(orderService.readOrders(pageRequestDto))
                .willReturn(pageResponseDto);

        // when & then
        assertThat(mvc.get().uri("/api/orders")
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk();
//                .bodyJson()
//                .extractingPath("$.result")
//                .asArray()
//                .hasSize(3);
    }

    @BeforeEach
    void setUp() {
        uuids = new UUID[18];
        for (int i = 0; i < uuids.length; i++) {
            uuids[i] = UUID.randomUUID();
        }
        dtos = Arrays.array(
                new ResponseDto(
                        uuids[0],
                        uuids[3],
                        uuids[4],
                        uuids[5],
                        "PENDING",
                        "",
                        LocalDateTime.of(2025, 3, 17, 13, 1, 1),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 1),
                        List.of(new OrderProductDto(
                                uuids[6],
                                uuids[7],
                                10000,
                                1
                        ))
                ),
                new ResponseDto(
                        uuids[1],
                        uuids[8],
                        uuids[9],
                        uuids[10],
                        "PENDING",
                        "",
                        LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                        List.of(new OrderProductDto(
                                uuids[11],
                                uuids[12],
                                20000,
                                2
                        ))
                ),
                new ResponseDto(
                        uuids[2],
                        uuids[13],
                        uuids[14],
                        uuids[15],
                        "PENDING",
                        "",
                        LocalDateTime.of(2025, 3, 17, 13, 1, 3),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 3),
                        List.of(new OrderProductDto(
                                uuids[16],
                                uuids[17],
                                30000,
                                3
                        ))
                )
        );
    }
}