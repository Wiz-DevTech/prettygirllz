package com.socialcommerce.chat.api;

import com.socialcommerce.chat.domain.ChatMessage;
import com.socialcommerce.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * REST API Controller for handling chat operations
 * Manages product-related conversations between users
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * Send a new chat message
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendMessage(
            @Valid @RequestBody ChatMessage message,
            Principal principal) {
        log.debug("Sending message from user: {}", principal.getName());
        ChatMessage sentMessage = chatService.sendMessage(message, principal.getName());
        return ResponseEntity.ok(sentMessage);
    }

    /**
     * Get chat history for a conversation
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<ChatMessage>> getConversationHistory(
            @PathVariable Long conversationId,
            Pageable pageable) {
        log.debug("Fetching conversation history for ID: {}", conversationId);
        Page<ChatMessage> messages = chatService.getConversationHistory(conversationId, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * WebSocket endpoint for real-time chat
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage broadcastMessage(@Payload ChatMessage message) {
        log.debug("Broadcasting message: {}", message);
        return chatService.processAndBroadcast(message);
    }

    /**
     * Mark messages as read
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId, Principal principal) {
        log.debug("Marking message {} as read by user: {}", messageId, principal.getName());
        chatService.markAsRead(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
