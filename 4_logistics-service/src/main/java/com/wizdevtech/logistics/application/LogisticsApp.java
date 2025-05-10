package com.wizdevtech.logistics.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wizdevtech.logistics"})  // Scans all components in the main package
@EnableJpaRepositories("com.wizdevtech.logistics.infrastructure.repository")  // Points to repository interfaces
@EntityScan("com.wizdevtech.logistics.infrastructure.entity")  // Updated to point to entity classes
public class LogisticsApp {
    public static void main(String[] args) {
        SpringApplication.run(LogisticsApp.class, args);
    }
}