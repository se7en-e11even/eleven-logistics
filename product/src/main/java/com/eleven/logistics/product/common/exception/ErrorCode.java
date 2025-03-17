package com.eleven.logistics.product.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus httpStatus();

    String message();
}
