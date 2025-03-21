package com.eleven.logistics.product;

import com.eleven.logistics.product.presentation.dto.request.CreateProductRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductApplicationTests {

	private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16.3");

	@DynamicPropertySource
	static void configureDatabase(DynamicPropertyRegistry registry) {
		container.start();
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.username", container::getUsername);
		registry.add("spring.datasource.password", container::getPassword);
	}

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("상품 생성 성공 시 201 created")
	void createProduct() {
		var requestProduct = new CreateProductRequest(
				"product1",
				10000,
				10
		);

		webTestClient.post()
				.uri("/api/products")
				.header("X-Username", "tester")
				.header("X-Role", "TESTER")
				.bodyValue(requestProduct)
				.exchange()
				.expectStatus().isCreated();

//		리턴 값이 있는 경우
//	.expectBody(반환되는 객체.class).value(actual -> {
//			assertThat(actual).isNotNull();
//			assertThat(actual.getValue()).isEqualTo(expected.getValue());
//		});
	}

	@AfterAll
	static void tearDown() {
		container.stop();
	}
}
