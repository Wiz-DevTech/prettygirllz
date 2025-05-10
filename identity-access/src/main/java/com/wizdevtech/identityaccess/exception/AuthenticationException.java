package com.wizdevtech.identityaccess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for authentication-related errors.
 * This exception is automatically mapped to HTTP 401 UNAUTHORIZED responses.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new authentication exception with the specified message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new authentication exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /*
     * The following static factory methods provide standardized ways to create
     * authentication exceptions for common scenarios. While currently not used,
     * they are maintained for future implementation consistency.
     */

    /**
     * Creates an exception for invalid credentials.
     * @return a new authentication exception
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid credentials");
    }

    /**
     * Creates an exception when a user cannot be found.
     * @param email the email that was not found
     * @return a new authentication exception
     */
    public static AuthenticationException userNotFound(String email) {
        return new AuthenticationException("User not found with email: " + email);
    }

    /**
     * Creates an exception for expired authentication tokens.
     * @return a new authentication exception
     */
    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("Authentication token has expired");
    }

    /**
     * Creates an exception for invalid authentication tokens.
     * @return a new authentication exception
     */
    public static AuthenticationException invalidToken() {
        return new AuthenticationException("Invalid authentication token");
    }
}