package com.eleven.logistics.product.presentation.dto;

import com.eleven.logistics.product.presentation.dto.request.CreateProductRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * product 생성 요청(ProductRequestDto valid 검증 테스트
 */
class CreateProductRequestValidationTest {

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
        var product = new CreateProductRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "product1",
                10000,
                10
        );

        // when
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(product);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 company_id 값으로 생성")
    void validateCompanyIdNull() {
        // given
        var product = new CreateProductRequest(
                null,
                UUID.randomUUID(),
                "product1",
                1000,
                1
        );

        // when
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(product);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("company_id 는 필수 항목입니다.");
    }
}