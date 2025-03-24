package com.eleven.logistics.product.domain.exception;

import lombok.Getter;

/**
 * 도메인에서 발생하는 예외는 도메인 계층에서 정의해야 한다.
 * CustomException 과 같은 비즈니스 로직 관련 예외는
 * 어떤 인터페이스(웹, API, 메시지 큐 등)에서도 사용될 수 있기 때문에
 * 도메인에 위치하는 것이 맞다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }
}