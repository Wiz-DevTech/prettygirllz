package com.socialcommerce.moderation;

import com.socialcommerce.moderation.domain.ModerationDecision;
import com.socialcommerce.moderation.domain.ModerationStatus;
import com.socialcommerce.moderation.repository.ModerationRepository;
import com.socialcommerce.moderation.rules.ContentRules;
import com.socialcommerce.moderation.service.ModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Moderation Service
 */
@ExtendWith(MockitoExtension.class)
class ModerationServiceTest {

    @Mock
    private ModerationRepository moderationRepository;

    @Mock
    private ContentRules contentRules;

    @InjectMocks
    private ModerationService moderationService;

    private ModerationDecision testDecision;

    @BeforeEach
    void setUp() {
        testDecision = ModerationDecision.builder()
            .id(1L)
            .contentId(100L)
            .contentType("FEED_ITEM")
            .status(ModerationStatus.APPROVED)
            .reason("Content passed automated checks")
            .build();
    }

    @Test
    void moderateContent_ShouldReturnDecision() {
        // Given
        when(contentRules.analyzeContent(any())).thenReturn(testDecision);
        when(moderationRepository.save(any())).thenReturn(testDecision);

        // When
        ModerationDecision result = moderationService.moderateContent(100L, "FEED_ITEM", "Test content");

        // Then
        assertNotNull(result);
        assertEquals(ModerationStatus.APPROVED, result.getStatus());
    }

    @Test
    void getDecision_ShouldReturnExistingDecision() {
        // Given
        when(moderationRepository.findByContentIdAndContentType(100L, "FEED_ITEM"))
            .thenReturn(Optional.of(testDecision));

        // When
        ModerationDecision result = moderationService.getDecision(100L, "FEED_ITEM");

        // Then
        assertNotNull(result);
        assertEquals(testDecision.getId(), result.getId());
    }
}
