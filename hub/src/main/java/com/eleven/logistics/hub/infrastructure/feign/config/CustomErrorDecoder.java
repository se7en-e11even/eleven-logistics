package com.eleven.logistics.hub.infrastructure.feign.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody = "응답 본문 없음";

        try {
            // 응답 본문이 있는 경우에만 읽기 시도
            if (response.body() != null) {
                responseBody = IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
                log.error("API 오류 응답: {}", responseBody);
            } else {
                log.error("API 응답 본문이 없습니다. 상태 코드: {}", response.status());
            }
        } catch (IOException e) {
            log.error("API 응답 처리 중 예외 발생", e);
            return new RuntimeException("API 오류 응답 처리 실패: " + e.getMessage(), e);
        }

        // 상태 코드에 따른 적절한 예외 반환
        switch (response.status()) {
            case 400:
                return new IllegalArgumentException("잘못된 요청: " + responseBody);
            case 401:
                return new SecurityException("인증 실패: " + responseBody);
            case 403:
                return new SecurityException("접근 권한 없음: " + responseBody);
            case 404:
                return new RuntimeException("리소스를 찾을 수 없습니다: " + responseBody);
            default:
                return new RuntimeException("API 호출 실패 (상태 코드: " + response.status() + "): " + responseBody);
        }
    }
}