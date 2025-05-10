package com.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * # Main Spring Boot application class
 */
@SpringBootApplication
@EnableJpaAuditing
public class ProductCatalogApplication {

    /**
     * Main method that starts the Spring Boot application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}