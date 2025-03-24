package com.eleven.logistics.hub.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "jpaAuditorAware")
public class JpaConfig {
}

