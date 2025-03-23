package com.eleven.logistics.product.domain.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus httpStatus();

    String message();
}
