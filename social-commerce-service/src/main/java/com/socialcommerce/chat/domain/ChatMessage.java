package com.socialcommerce.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a chat message in product conversations
 * Stores message content, sender, timestamp, and product reference
 */
@Data
@Entity
@Table(name = "chat_messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @NotNull
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "conversation_id")
    private Long conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType type = MessageType.TEXT;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Message type enumeration
     */
    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }
}
