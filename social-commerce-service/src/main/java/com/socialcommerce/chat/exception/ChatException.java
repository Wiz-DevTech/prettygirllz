package com.socialcommerce.chat.exception;

/**
 * Custom exception for chat-related errors
 * Handles WebSocket connection and message delivery failures
 */
public class ChatException extends RuntimeException {
    
    public ChatException(String message) {
        super(message);
    }
    
    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ChatException(Throwable cause) {
        super(cause);
    }
}
