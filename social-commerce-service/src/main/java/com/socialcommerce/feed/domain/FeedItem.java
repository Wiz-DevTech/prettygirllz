package com.socialcommerce.feed.domain;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a feed item in the community feed
 * Contains product reviews, recommendations, and user posts
 */
@Data
@Entity
@Table(name = "feed_items")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedType type;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "feed_item_tags", joinColumns = @JoinColumn(name = "feed_item_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "shares_count")
    private Integer sharesCount = 0;

    @Column(name = "engagement_score")
    private Double engagementScore = 0.0;

    @OneToMany(mappedBy = "feedItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeedComment> comments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "feed_item_likes", joinColumns = @JoinColumn(name = "feed_item_id"))
    @Column(name = "user_id")
    private Set<Long> likedBy = new HashSet<>();

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Feed item type enumeration
     */
    public enum FeedType {
        PRODUCT_REVIEW,
        RECOMMENDATION,
        QUESTION,
        DISCUSSION,
        ANNOUNCEMENT,
        USER_POST
    }

    /**
     * Visibility enumeration
     */
    public enum Visibility {
        PUBLIC, FRIENDS_ONLY, PRIVATE
    }

    /**
     * Nested comment entity
     */
    @Entity
    @Table(name = "feed_comments")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedComment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "feed_item_id")
        private FeedItem feedItem;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String content;

        @CreationTimestamp
        @Column(name = "created_at")
        private LocalDateTime createdAt;
    }
}
