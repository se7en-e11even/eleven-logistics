package com.eleven.logistics.order.presentation.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("유효한 값으로 생성")
    void validate() {
        // given
        var order = new CreateRequestDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "",
                List.of(
                        new CreateRequestDto.OrderProductCreateDto(
                                UUID.randomUUID(),
                                1000,
                                1
                        )
                )
        );

        // when
        Set<ConstraintViolation<CreateRequestDto>> violations = validator.validate(order);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 값으로 생성")
    void validate_null() {
        // given
        var order = new CreateRequestDto(
                null,
                UUID.randomUUID(),
                "",
                List.of(
                        new CreateRequestDto.OrderProductCreateDto(
                                UUID.randomUUID(),
                                1000,
                                0
                        )
                )
        );

        // when
        Set<ConstraintViolation<CreateRequestDto>> violations = validator.validate(order);

        // then
        assertThat(violations).hasSize(2);

        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        assertThat(messages).contains(
                "supply_id 는 필수 항목입니다.",
                "수량은 양수입니다."
        );
    }
}