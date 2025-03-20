package com.eleven.logistics.product.presentation;

import com.eleven.logistics.product.application.service.ProductService;
import com.eleven.logistics.product.application.dto.command.CreateProductCommand;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.common.exception.CustomException;
import com.eleven.logistics.product.presentation.controller.ProductController;
import com.eleven.logistics.product.presentation.dto.request.CreateProductRequest;
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
import java.util.UUID;

import static com.eleven.logistics.product.domain.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(ProductController.class)
class ProductControllerMvcTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private FindProductQuery[] dtos;

    UUID[] randomId;

    @Test
    @DisplayName("상품 생성 요청 성공 테스트")
    void create() throws JsonProcessingException {
        UUID productId = UUID.randomUUID();
        var createRequestProduct = new CreateProductRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "product1",
                10000,
                10
        );

        var createRequest = objectMapper.writeValueAsString(createRequestProduct);

        given(productService.create(any(CreateProductCommand.class)))
                .willReturn(productId);

        // when & then
        assertThat(mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Username", "tester")
                .header("X-Role", "TESTER")
                .content(createRequest)
        ).hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", "/api/products/" + productId);

    }

    @Test
    @DisplayName("요청한 상품이 없으면 404 NotFound")
    void read_ById_not_found() {
        UUID productId = UUID.randomUUID();
        given(productService.read(productId))
                .willThrow(new CustomException(PRODUCT_NOT_FOUND));

        // when & then
        assertThat(mvc.get().uri("/api/products/" + productId)
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("상품 상세 조회")
    void readById() {
        given(productService.read(randomId[0]))
                .willReturn(dtos[0]);

        // when & then
        assertThat(mvc.get().uri("/api/products/" + randomId[0])
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk();
    }

    @Test
    @DisplayName("상품 목록 조회")
    void retreiveProducts() {}

    @BeforeEach
    void setUp() {
        randomId = new UUID[9];
        for (int i = 0; i < randomId.length; i++) {
            randomId[i] = UUID.randomUUID();
        }
        dtos = Arrays.array(
                new FindProductQuery(
                        randomId[0],
                        randomId[3],
                        randomId[6],
                        "product1",
                        1000,
                        1,
                        LocalDateTime.of(2025, 3, 17, 13, 1, 1),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 1)
                ),
                new FindProductQuery(
                        randomId[1],
                        randomId[4],
                        randomId[7],
                        "product2",
                        2000,
                        2,
                        LocalDateTime.of(2025, 3, 17, 13, 1, 2),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 2)
                ),
                new FindProductQuery(
                        randomId[2],
                        randomId[5],
                        randomId[8],
                        "product3",
                        3000,
                        3,
                        LocalDateTime.of(2025, 3, 17, 13, 1, 3),
                        LocalDateTime.of(2025, 3, 17, 13, 1, 3)
                )

        );
    }
}