package com.eleven.logistics.order.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus httpStatus();

    String message();
}
