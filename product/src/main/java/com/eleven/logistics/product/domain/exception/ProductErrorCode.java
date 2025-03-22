package com.eleven.logistics.product.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 상품을 찾을 수 없습니다."),
    PRODUCT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다"),

    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 업체의 상품입니다."),

    HUB_NOT_FOUND(HttpStatus.BAD_REQUEST, "상품의 관리허브가 존재하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String message() {
        return message;
    }
    }
