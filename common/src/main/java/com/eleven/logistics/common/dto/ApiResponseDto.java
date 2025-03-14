package com.eleven.logistics.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    private Integer code; // 응답 코드
    private String message; // 응답 메시지
    private T data; // 응답 데이터

    // 성공 응답을 위한 메서드
    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .code(Code.SUCCESS.getCode())
                .message(Code.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    // 성공 응답을 위한 메시지 포함 메서드
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return ApiResponseDto.<T>builder()
                .code(Code.SUCCESS.getCode())
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답을 위한 메서드
    public static <T> ApiResponseDto<T> failure(Integer code, String message) {
        return ApiResponseDto.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    // of 메서드를 사용하여 ApiResponseDto 객체를 생성하는 메서드
    public static <T> ApiResponseDto<T> failure(Integer code, String message, T data) {
        return ApiResponseDto.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
