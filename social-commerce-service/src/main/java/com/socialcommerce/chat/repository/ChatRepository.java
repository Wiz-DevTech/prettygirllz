package com.socialcommerce.chat.repository;

import com.socialcommerce.chat.domain.ChatMessage;
import com.socialcommerce.chat.domain.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for chat message persistence
 * Provides CRUD operations for chat messages
 */
@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find messages by conversation ID
     */
    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    /**
     * Find unread messages for a user
     */
    List<ChatMessage> findByRecipientIdAndStatus(Long recipientId, MessageStatus status);

    /**
     * Find messages between two users
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.recipientId = :userId1) " +
           "ORDER BY m.createdAt DESC")
    Page<ChatMessage> findConversationBetween(@Param("userId1") Long userId1, 
                                              @Param("userId2") Long userId2, 
                                              Pageable pageable);

    /**
     * Update message status
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = :status, m.readAt = :readAt " +
           "WHERE m.id = :messageId AND m.recipientId = :recipientId")
    void updateMessageStatus(@Param("messageId") Long messageId, 
                            @Param("recipientId") Long recipientId,
                            @Param("status") MessageStatus status,
                            @Param("readAt") LocalDateTime readAt);

    /**
     * Find messages for a product discussion
     */
    Page<ChatMessage> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    /**
     * Count unread messages for a user
     */
    long countByRecipientIdAndStatus(Long recipientId, MessageStatus status);
}
