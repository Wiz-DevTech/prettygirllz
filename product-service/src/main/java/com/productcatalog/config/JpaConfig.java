package com.productcatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.Optional;

/**
 * Configuration class for JPA auditing.
 * This replaces the previous MongoConfig and handles entity auditing.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    /**
     * Provides the current auditor (user) for entity auditing.
     * Uses @ConditionalOnMissingBean to prevent duplicate bean definitions.
     *
     * @return AuditorAware implementation that returns "system" as default user
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> auditorProvider() {
        // In a real application, this would return the current authenticated user
        return () -> Optional.of("system");
    }
}