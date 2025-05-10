package com.socialcommerce.feed.service;

import com.socialcommerce.feed.domain.FeedItem;
import com.socialcommerce.feed.ranking.RecommendationEngine;
import com.socialcommerce.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for feed business logic
 * Handles content aggregation and personalization
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final RecommendationEngine recommendationEngine;

    /**
     * Get personalized feed for user
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "personalizedFeed", key = "#username + #pageable.pageNumber")
    public Page<FeedItem> getPersonalizedFeed(String username, Pageable pageable) {
        // Get user preferences (simplified - would normally come from user service)
        List<Long> followedUsers = getFollowedUsers(username);
        List<String> userInterests = getUserInterests(username);
        
        // Get base feed items
        Page<FeedItem> baseFeed = feedRepository.findPersonalizedFeed(followedUsers, userInterests, pageable);
        
        // Apply recommendation algorithm for ranking
        List<FeedItem> rankedItems = recommendationEngine.rankFeedItems(
            baseFeed.getContent(), 
            username
        );
        
        return new PageImpl<>(rankedItems, pageable, baseFeed.getTotalElements());
    }

    /**
     * Create a new feed item
     */
    @Transactional
    public FeedItem createFeedItem(FeedItem feedItem, String username) {
        // Set user ID (would normally get from user service)
        // feedItem.setUserId(getUserIdFromUsername(username));
        
        // Save feed item
        FeedItem savedItem = feedRepository.save(feedItem);
        
        // Update engagement score
        updateEngagementScore(savedItem.getId());
        
        log.info("Created feed item {} by user {}", savedItem.getId(), username);
        return savedItem;
    }

    /**
     * Get trending items
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "trendingItems", key = "#hours")
    public List<FeedItem> getTrendingItems(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return feedRepository.findTrendingItems(since, Pageable.ofSize(20));
    }

    /**
     * Get feed item by ID
     */
    @Transactional(readOnly = true)
    public FeedItem getFeedItem(Long itemId) {
        return feedRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Feed item not found"));
    }

    /**
     * Like a feed item
     */
    @Transactional
    public void likeFeedItem(Long itemId, String username) {
        FeedItem item = getFeedItem(itemId);
        // Long userId = getUserIdFromUsername(username);
        
        // Add like if not already liked
        // if (item.getLikedBy().add(userId)) {
        //     item.setLikesCount(item.getLikesCount() + 1);
        //     feedRepository.save(item);
        //     updateEngagementScore(itemId);
        // }
    }

    /**
     * Add comment to feed item
     */
    @Transactional
    public FeedItem addComment(Long itemId, String comment, String username) {
        FeedItem item = getFeedItem(itemId);
        // Long userId = getUserIdFromUsername(username);
        
        FeedItem.FeedComment feedComment = new FeedItem.FeedComment();
        // feedComment.setUserId(userId);
        feedComment.setContent(comment);
        feedComment.setFeedItem(item);
        
        item.getComments().add(feedComment);
        item.setCommentsCount(item.getCommentsCount() + 1);
        
        FeedItem updatedItem = feedRepository.save(item);
        updateEngagementScore(itemId);
        
        return updatedItem;
    }

    /**
     * Get feed by category
     */
    @Transactional(readOnly = true)
    public Page<FeedItem> getFeedByCategory(String category, Pageable pageable) {
        return feedRepository.findByTag(category, pageable);
    }

    /**
     * Search feed items
     */
    @Transactional(readOnly = true)
    public Page<FeedItem> searchFeedItems(String query, Pageable pageable) {
        return feedRepository.searchByContent(query, pageable);
    }

    /**
     * Update engagement score for an item
     */
    private void updateEngagementScore(Long itemId) {
        feedRepository.updateEngagementScore(itemId);
    }

    /**
     * Get followed users (mock implementation)
     */
    private List<Long> getFollowedUsers(String username) {
        // Would normally query user service
        return List.of(1L, 2L, 3L);
    }

    /**
     * Get user interests (mock implementation)
     */
    private List<String> getUserInterests(String username) {
        // Would normally query user service
        return List.of("electronics", "fashion", "books");
    }
}
