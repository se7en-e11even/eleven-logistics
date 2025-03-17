package com.eleven.logistics.hub.presentation.exception;

import com.eleven.logistics.common.dto.ApiResponseDto;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 Not Found 예외 처리
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto<String> handleNotFoundException(Exception ex) {
        return ApiResponseDto.failure(404, "요청한 API를 찾을 수 없습니다.",ex.getMessage());
    }

    // 데이터 무결성 오류 (중복 키 제약 조건 위반)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // HTTP 400 - 잘못된 요청
    public ApiResponseDto<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        return ApiResponseDto.failure(400, message);
    }

    // 데이터 무결성 오류 (잘못된 입력)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // HTTP 400 - 잘못된 요청
    public ApiResponseDto<String> handleDataIntegrityViolationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())  // 디폴트 메시지 추출
                .findFirst()  // 첫 번째 메시지만 추출
                .orElse("유효성 검사 실패");  // 만약 메시지가 없다면 기본 메시지
        return ApiResponseDto.failure(400, errorMessage, ex.getMessage());
    }

    // 공통적인 500 Internal Server Error 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseDto<Object> handleGlobalException(Exception ex) {
        // 개발 중에는 상세 에러 정보 반환, 운영 환경에서는 기본 메시지만 반환
        boolean isDevMode = true; // 개발 환경 여부 (운영에서는 false로 설정)
        if (isDevMode) {
            // 스택 트레이스를 문자열로 변환하여 반환
            List<String> stackTrace = Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .limit(5) // 너무 길면 5줄까지만 반환
                    .toList();
            return ApiResponseDto.failure(500, "서버 내부 오류가 발생했습니다.", stackTrace);
        } else {
            return ApiResponseDto.failure(500, "서버 내부 오류가 발생했습니다.");
        }
    }

    // GlobalExceptionHandler 수정
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponseDto.failure(404, ex.getMessage());  // 예외 메시지 그대로 반환
    }
}

