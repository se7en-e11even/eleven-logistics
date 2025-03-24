package com.eleven.logistics.slack.common.config;

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
        try {
            if (response.status() == 404) {
                return new RuntimeException("리소스를 찾을 수 없습니다.");
            }

            String responseBody = IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
            log.error("API 오류 응답: {}", responseBody);

            return new RuntimeException("API 호출 실패: " + response.status());
        } catch (IOException e) {
            return new RuntimeException("API 오류 처리 실패", e);
        }
    }
}
