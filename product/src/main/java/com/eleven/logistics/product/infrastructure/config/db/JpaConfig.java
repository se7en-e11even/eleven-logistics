package com.eleven.logistics.product.infrastructure.config.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "jpaAuditorAware")
public class JpaConfig {
}
