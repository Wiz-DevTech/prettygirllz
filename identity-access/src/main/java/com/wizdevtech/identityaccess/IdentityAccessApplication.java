// src/main/java/com/wizdevtech/identityaccess/IdentityAccessApplication.java
package com.wizdevtech.identityaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.wizdevtech.identityaccess.model")
@EnableJpaRepositories("com.wizdevtech.identityaccess.repository")
@EnableJpaAuditing
public class IdentityAccessApplication {
    private static final Logger logger = LoggerFactory.getLogger(IdentityAccessApplication.class);

    @Value("${encryption.key:}")
    private String encryptionKey;

    private final Environment env;

    public IdentityAccessApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        try {
            System.out.println("=== Starting Application ===");
            String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
            System.out.println("Active profiles: " + (activeProfiles != null ? activeProfiles : "none"));

            // Verify environment variables are present
            String envKey = System.getenv("ENCRYPTION_KEY");
            System.out.println("ENCRYPTION_KEY from env: " + (envKey != null ? "[PRESENT]" : "[MISSING]"));

            SpringApplication.run(IdentityAccessApplication.class, args);

            System.out.println("=== Application Started Successfully ===");
        } catch (Exception e) {
            System.err.println("!!! APPLICATION FAILED TO START !!!");
            System.err.println("Root cause: " + getRootCause(e).getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("=== Configuration Verification ===");
            System.out.println("Database URL: " + maskSensitiveUrl(env.getProperty("spring.datasource.url")));
            System.out.println("Server Port: " + env.getProperty("server.port"));

            // Verify encryption key without logging its value
            System.out.println("Encryption key from config: " + (encryptionKey != null && !encryptionKey.isEmpty() ? "[PRESENT]" : "[MISSING]"));

            if (encryptionKey != null && !encryptionKey.isEmpty()) {
                try {
                    byte[] keyBytes = Base64.getDecoder().decode(encryptionKey.trim());
                    System.out.println("Encryption key length: " + keyBytes.length + " bytes");
                    if (keyBytes.length != 32) {
                        System.err.println("ERROR: Invalid encryption key length. Expected 32 bytes, got " + keyBytes.length);
                        throw new IllegalStateException("Invalid encryption key length. Expected 32 bytes, got " + keyBytes.length);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("ERROR: Invalid Base64 encoding for encryption key");
                    throw new IllegalStateException("Invalid Base64 encoding for encryption key", e);
                }
            } else {
                System.out.println("WARNING: Encryption key is missing");
            }

            System.out.println("=== Configuration Verified ===");
        } catch (Exception e) {
            System.err.println("!!! CONFIGURATION VERIFICATION FAILED !!!");
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private static Throwable getRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    private String maskSensitiveUrl(String url) {
        if (url == null) return null;
        // Mask username and password in JDBC URL if present
        return url.replaceAll("://([^:]+):([^@]+)@", "://[USERNAME]:[PASSWORD]@");
    }
}