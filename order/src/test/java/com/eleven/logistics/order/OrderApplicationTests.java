package com.eleven.logistics.order;

import com.eleven.logistics.order.presentation.dto.CreateRequestDto;
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

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderApplicationTests {

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
	@DisplayName("주문 성공 시 201")
	void createOrder() {
		// given
		var requestOrder = new CreateRequestDto(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"",
				List.of(new CreateRequestDto.OrderProductCreateDto(
						UUID.randomUUID(),
						1000,
						1)
				)
		);

		// when & then
		webTestClient.post()
				.uri("/api/orders")
				.bodyValue(requestOrder)
				.exchange()
				.expectStatus().isCreated();
	}

	@AfterAll
	static void tearDown() {
		container.stop();
	}
}
