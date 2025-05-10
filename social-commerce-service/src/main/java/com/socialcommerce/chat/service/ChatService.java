package com.socialcommerce.chat.service;

import com.socialcommerce.chat.domain.ChatMessage;
import com.socialcommerce.chat.domain.MessageStatus;
import com.socialcommerce.chat.exception.ChatException;
import com.socialcommerce.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for chat business logic
 * Handles message processing, storage, and retrieval
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a new chat message
     */
    @Transactional
    public ChatMessage sendMessage(ChatMessage message, String senderUsername) {
        try {
            // Set sender details (would normally get from user service)
            // message.setSenderId(getUserIdFromUsername(senderUsername));
            
            // Save message
            ChatMessage savedMessage = chatRepository.save(message);
            
            // Send to recipient via WebSocket
            messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getRecipientId()),
                "/queue/messages",
                savedMessage
            );
            
            log.info("Message sent from {} to {}", message.getSenderId(), message.getRecipientId());
            return savedMessage;
            
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            throw new ChatException("Failed to send message", e);
        }
    }

    /**
     * Process and broadcast message via WebSocket
     */
    public ChatMessage processAndBroadcast(ChatMessage message) {
        // Process message (validation, filtering, etc.)
        message.setCreatedAt(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);
        
        // Save to database
        ChatMessage savedMessage = chatRepository.save(message);
        
        // Broadcast to specific topic or user
        if (message.getConversationId() != null) {
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.getConversationId(),
                savedMessage
            );
        }
        
        return savedMessage;
    }

    /**
     * Get conversation history
     */
    @Transactional(readOnly = true)
    public Page<ChatMessage> getConversationHistory(Long conversationId, Pageable pageable) {
        return chatRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    /**
     * Mark message as read
     */
    @Transactional
    public void markAsRead(Long messageId, String readerUsername) {
        // Get user ID from username (would normally use user service)
        // Long readerId = getUserIdFromUsername(readerUsername);
        
        ChatMessage message = chatRepository.findById(messageId)
            .orElseThrow(() -> new ChatException("Message not found"));
            
        // Verify the reader is the recipient
        // if (!message.getRecipientId().equals(readerId)) {
        //     throw new ChatException("Unauthorized to mark this message as read");
        // }
        
        chatRepository.updateMessageStatus(
            messageId, 
            message.getRecipientId(), 
            MessageStatus.READ, 
            LocalDateTime.now()
        );
        
        // Notify sender that message was read
        messagingTemplate.convertAndSendToUser(
            String.valueOf(message.getSenderId()),
            "/queue/read-receipts",
            messageId
        );
    }

    /**
     * Get unread messages for a user
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getUnreadMessages(Long userId) {
        return chatRepository.findByRecipientIdAndStatus(userId, MessageStatus.SENT);
    }

    /**
     * Get conversation between two users
     */
    @Transactional(readOnly = true)
    public Page<ChatMessage> getConversationBetween(Long userId1, Long userId2, Pageable pageable) {
        return chatRepository.findConversationBetween(userId1, userId2, pageable);
    }

    /**
     * Get unread message count
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return chatRepository.countByRecipientIdAndStatus(userId, MessageStatus.SENT);
    }
}
