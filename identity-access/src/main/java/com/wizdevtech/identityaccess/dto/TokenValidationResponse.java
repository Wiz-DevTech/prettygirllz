package com.wizdevtech.identityaccess.dto;

public class TokenValidationResponse {
    private boolean valid;
    private String message;
    private String error;

    // Default constructor
    public TokenValidationResponse() {}

    // Full constructor
    public TokenValidationResponse(boolean valid, String message, String error) {
        this.valid = valid;
        this.message = message;
        this.error = error;
    }

    // Builder pattern constructor (optional)
    public static TokenValidationResponse builder() {
        return new TokenValidationResponse();
    }

    // Getters and setters
    public boolean isValid() {
        return valid;
    }

    public TokenValidationResponse setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TokenValidationResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getError() {
        return error;
    }

    public TokenValidationResponse setError(String error) {
        this.error = error;
        return this;
    }

    // Builder-style methods
    public TokenValidationResponse withValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public TokenValidationResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public TokenValidationResponse withError(String error) {
        this.error = error;
        return this;
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "TokenValidationResponse{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}