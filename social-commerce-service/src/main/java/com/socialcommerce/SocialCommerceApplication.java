package com.socialcommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class
 * Entry point for the Social Commerce Service
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class SocialCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialCommerceApplication.class, args);
    }
}
