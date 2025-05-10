package com.wizdevtech.identityaccess.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Application-wide configuration class for beans and services
 * that are not specific to security concerns.
 */
@Configuration
public class AppConfig {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Value("${app.async.core-pool-size:10}")
    private int corePoolSize;

    @Value("${app.rest.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${app.rest.read-timeout:10000}")
    private int readTimeout;

    /**
     * Configures a REST template for external API calls with custom timeout settings
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }

    /**
     * Provides an ObjectMapper for JSON serialization/deserialization
     * with support for Java 8 date/time types
     * @return Configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Add Java 8 time module support
        mapper.registerModule(new JavaTimeModule());
        // Prevent timestamps from being written as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Provides a thread pool executor for async operations
     * @return Thread pool executor for asynchronous tasks
     */
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }

    /**
     * Configures the encryption cipher used by the application
     * using the encryption key defined in properties
     * @return AES GCM cipher instance
     * @throws NoSuchPaddingException if the requested padding mechanism is not available
     * @throws NoSuchAlgorithmException if the requested algorithm is not available
     */
    @Bean
    public Cipher aesCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/GCM/NoPadding");
    }

    /**
     * Provides the secret key specification for AES encryption/decryption
     * @return SecretKeySpec for AES operations
     */
    @Bean
    public SecretKeySpec secretKeySpec() {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        return new SecretKeySpec(keyBytes, "AES");
    }
}