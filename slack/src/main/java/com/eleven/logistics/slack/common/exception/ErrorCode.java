package com.eleven.logistics.slack.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus httpStatus();

    String message();
}
