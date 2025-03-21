package com.eleven.logistics.product.infrastructure.config;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Application.java 파일에 설정하지 않으면 스캔할 기본 패키지를 지정해 줘야 한다.
@Configuration
@EnableFeignClients(basePackages = "com.eleven.logistics.product")
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 요청, 응답 전체 로깅
        return Logger.Level.FULL;
    }
}
