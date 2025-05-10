package com.socialcommerce.feed.repository;

import com.socialcommerce.feed.domain.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for feed item persistence
 * Provides paginated queries for feed content
 */
@Repository
public interface FeedRepository extends JpaRepository<FeedItem, Long> {

    /**
     * Find feed items by user ID
     */
    Page<FeedItem> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find public feed items
     */
    Page<FeedItem> findByVisibilityOrderByCreatedAtDesc(FeedItem.Visibility visibility, Pageable pageable);

    /**
     * Find feed items by type
     */
    Page<FeedItem> findByTypeOrderByCreatedAtDesc(FeedItem.FeedType type, Pageable pageable);

    /**
     * Find feed items by tag
     */
    @Query("SELECT f FROM FeedItem f JOIN f.tags t WHERE t = :tag ORDER BY f.createdAt DESC")
    Page<FeedItem> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Find trending items based on engagement score
     */
    @Query("SELECT f FROM FeedItem f WHERE f.createdAt > :since " +
           "ORDER BY f.engagementScore DESC, f.createdAt DESC")
    List<FeedItem> findTrendingItems(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Search feed items by content
     */
    @Query("SELECT f FROM FeedItem f WHERE LOWER(f.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY f.createdAt DESC")
    Page<FeedItem> searchByContent(@Param("query") String query, Pageable pageable);

    /**
     * Find personalized feed based on user preferences
     * This is a simplified version - in reality, you'd have a more complex algorithm
     */
    @Query("SELECT f FROM FeedItem f WHERE f.visibility = 'PUBLIC' " +
           "AND (f.userId IN :followedUsers OR f.tags IN :userInterests) " +
           "ORDER BY f.engagementScore DESC, f.createdAt DESC")
    Page<FeedItem> findPersonalizedFeed(@Param("followedUsers") List<Long> followedUsers, 
                                       @Param("userInterests") List<String> userInterests, 
                                       Pageable pageable);

    /**
     * Find feed items for a specific product
     */
    Page<FeedItem> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    /**
     * Update engagement score
     */
    @Query("UPDATE FeedItem f SET f.engagementScore = " +
           "(f.likesCount * 1.0 + f.commentsCount * 2.0 + f.sharesCount * 3.0) / " +
           "(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - f.createdAt)) / 3600 + 2) " +
           "WHERE f.id = :itemId")
    void updateEngagementScore(@Param("itemId") Long itemId);

    /**
     * Get featured items
     */
    List<FeedItem> findByIsFeaturedTrueOrderByCreatedAtDesc();
}
