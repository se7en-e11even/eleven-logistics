package com.eleven.logistics.product.common.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaAuditorAware implements AuditorAware<String> {

    private final HttpServletRequest request;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(request.getHeader("X-Username"));
    }
}
