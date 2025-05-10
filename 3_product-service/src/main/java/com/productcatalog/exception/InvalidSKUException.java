package com.productcatalog.exception;

/**
 * # Custom exception for invalid SKUs
 */
public class InvalidSKUException extends RuntimeException {

    public InvalidSKUException(String message) {
        super(message);
    }

    public InvalidSKUException(String message, Throwable cause) {
        super(message, cause);
    }
}