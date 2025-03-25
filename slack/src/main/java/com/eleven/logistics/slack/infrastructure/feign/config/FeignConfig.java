package com.eleven.logistics.slack.infrastructure.feign.config;

import com.eleven.logistics.slack.common.config.CustomErrorDecoder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.AMQP;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig implements RequestInterceptor {
    @Bean
    public Decoder feignDecoder() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return new ResponseEntityDecoder(new JacksonDecoder(objectMapper));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(() -> new HttpMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper())
        ));
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {

//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if (requestAttributes == null) {
//            requestTemplate.header("X-Role", "권한을 찾지 못했습니다.");
//            requestTemplate.header("X-Username", "사용자를 찾지 못했습니다.");
//        } else if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
//            HttpServletRequest request = servletRequestAttributes.getRequest();
//
//            String username = request.getHeader("X-Username");
//            String role = request.getHeader("X-Role");
//
//            if (username != null) {
//                requestTemplate.header("X-Username", username);
//            }
//            if (role != null) {
//                requestTemplate.header("X-Role", role);
//            }
//        }
    }
}