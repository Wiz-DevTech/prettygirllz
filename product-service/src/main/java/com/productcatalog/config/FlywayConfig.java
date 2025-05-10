package com.productcatalog.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * # Configuration for Flyway database migrations
 * # New for migrations
 */
@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfig {

    /**
     * Configures Flyway with custom settings
     */
    @Bean(initMethod = "migrate")
    @DependsOn("dataSource")
    public Flyway flyway(DataSource dataSource, FlywayProperties flywayProperties) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayProperties.getLocations().toArray(new String[0]))
                .baselineOnMigrate(flywayProperties.isBaselineOnMigrate())
                .load();
    }
}