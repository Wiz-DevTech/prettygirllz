package com.socialcommerce.chat;

import com.socialcommerce.chat.domain.ChatMessage;
import com.socialcommerce.chat.repository.ChatRepository;
import com.socialcommerce.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Chat Service
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = ChatMessage.builder()
            .id(1L)
            .senderId(100L)
            .recipientId(200L)
            .content("Test message")
            .build();
    }

    @Test
    void sendMessage_ShouldSaveAndReturnMessage() {
        // Given
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        // When
        ChatMessage result = chatService.sendMessage(testMessage, "testuser");

        // Then
        assertNotNull(result);
        assertEquals(testMessage.getContent(), result.getContent());
    }

    @Test
    void processAndBroadcast_ShouldSaveMessage() {
        // Given
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        // When
        ChatMessage result = chatService.processAndBroadcast(testMessage);

        // Then
        assertNotNull(result);
        assertEquals(testMessage.getId(), result.getId());
    }
}
