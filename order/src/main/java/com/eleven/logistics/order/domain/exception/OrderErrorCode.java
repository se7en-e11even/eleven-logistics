package com.eleven.logistics.order.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 주문을 찾을 수 없습니다."),
    ORDER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "업체가 존재하지 않습입니다."),

    HUB_NOT_FOUND(HttpStatus.BAD_REQUEST, "허브가 존재하지 않습니다."),
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
