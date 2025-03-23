package com.eleven.logistics.product.presentation.dto.response;

import com.eleven.logistics.product.domain.exception.ErrorCode;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
public record ErrorResponse(int status, String message) {

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.httpStatus().value())
                        .message(errorCode.message())
                        .build());
    }
}