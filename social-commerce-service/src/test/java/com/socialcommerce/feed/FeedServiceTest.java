package com.socialcommerce.feed;

import com.socialcommerce.feed.domain.FeedItem;
import com.socialcommerce.feed.ranking.RecommendationEngine;
import com.socialcommerce.feed.repository.FeedRepository;
import com.socialcommerce.feed.service.FeedService;
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
 * Unit tests for Feed Service
 */
@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private RecommendationEngine recommendationEngine;

    @InjectMocks
    private FeedService feedService;

    private FeedItem testFeedItem;

    @BeforeEach
    void setUp() {
        testFeedItem = FeedItem.builder()
            .id(1L)
            .userId(100L)
            .content("Test feed content")
            .type(FeedItem.FeedType.USER_POST)
            .build();
    }

    @Test
    void createFeedItem_ShouldSaveAndReturnItem() {
        // Given
        when(feedRepository.save(any(FeedItem.class))).thenReturn(testFeedItem);

        // When
        FeedItem result = feedService.createFeedItem(testFeedItem, "testuser");

        // Then
        assertNotNull(result);
        assertEquals(testFeedItem.getContent(), result.getContent());
    }

    @Test
    void getFeedItem_ShouldReturnItem() {
        // Given
        when(feedRepository.findById(1L)).thenReturn(Optional.of(testFeedItem));

        // When
        FeedItem result = feedService.getFeedItem(1L);

        // Then
        assertNotNull(result);
        assertEquals(testFeedItem.getId(), result.getId());
    }
}
