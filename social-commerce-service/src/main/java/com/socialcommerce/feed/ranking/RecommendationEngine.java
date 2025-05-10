package com.socialcommerce.feed.ranking;

import com.socialcommerce.feed.domain.FeedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Recommendation algorithm for personalized feed content
 * Uses user behavior and preferences to rank content
 */
@Slf4j
@Component
public class RecommendationEngine {

    /**
     * Rank feed items based on user preferences and engagement metrics
     */
    public List<FeedItem> rankFeedItems(List<FeedItem> items, String username) {
        // Get user profile and preferences (mock implementation)
        UserProfile userProfile = getUserProfile(username);
        
        // Calculate relevance score for each item
        Map<FeedItem, Double> itemScores = items.stream()
            .collect(Collectors.toMap(
                item -> item,
                item -> calculateRelevanceScore(item, userProfile)
            ));
        
        // Sort by relevance score
        return items.stream()
            .sorted(Comparator.comparing(item -> itemScores.get(item), Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for a feed item
     */
    private double calculateRelevanceScore(FeedItem item, UserProfile userProfile) {
        double score = 0.0;
        
        // Base engagement score
        score += item.getEngagementScore() * 0.3;
        
        // Recency factor
        long hoursAgo = java.time.Duration.between(item.getCreatedAt(), java.time.LocalDateTime.now()).toHours();
        double recencyFactor = Math.max(0, 1 - (hoursAgo / 168.0)); // Decay over a week
        score += recencyFactor * 0.2;
        
        // Interest matching
        double interestScore = calculateInterestMatch(item, userProfile);
        score += interestScore * 0.3;
        
        // Social signals (if user's connections engaged with this)
        double socialScore = calculateSocialSignals(item, userProfile);
        score += socialScore * 0.2;
        
        return score;
    }

    /**
     * Calculate interest match between item and user profile
     */
    private double calculateInterestMatch(FeedItem item, UserProfile userProfile) {
        if (item.getTags() == null || userProfile.getInterests() == null) {
            return 0.0;
        }
        
        long matchingTags = item.getTags().stream()
            .filter(tag -> userProfile.getInterests().contains(tag))
            .count();
            
        return matchingTags > 0 ? (double) matchingTags / item.getTags().size() : 0.0;
    }

    /**
     * Calculate social signals score
     */
    private double calculateSocialSignals(FeedItem item, UserProfile userProfile) {
        // Check if any of user's connections liked or commented
        long connectionEngagement = item.getLikedBy().stream()
            .filter(userId -> userProfile.getConnections().contains(userId))
            .count();
            
        return connectionEngagement > 0 ? Math.min(1.0, connectionEngagement / 5.0) : 0.0;
    }

    /**
     * Get user profile (mock implementation)
     */
    private UserProfile getUserProfile(String username) {
        // Would normally fetch from user service
        return UserProfile.builder()
            .username(username)
            .interests(List.of("technology", "fashion", "food"))
            .connections(List.of(1L, 2L, 3L, 4L, 5L))
            .build();
    }

    /**
     * Inner class representing user profile
     */
    @lombok.Builder
    @lombok.Data
    private static class UserProfile {
        private String username;
        private List<String> interests;
        private List<Long> connections;
    }
}
