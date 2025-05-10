package com.socialcommerce.feed.api;

import com.socialcommerce.feed.domain.FeedItem;
import com.socialcommerce.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * REST API Controller for community feed operations
 * Manages content discovery and personalized recommendations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /**
     * Get personalized feed for user
     */
    @GetMapping
    public ResponseEntity<Page<FeedItem>> getPersonalizedFeed(
            Principal principal,
            Pageable pageable) {
        log.debug("Fetching personalized feed for user: {}", principal.getName());
        Page<FeedItem> feed = feedService.getPersonalizedFeed(principal.getName(), pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Create a new feed item
     */
    @PostMapping("/items")
    public ResponseEntity<FeedItem> createFeedItem(
            @Valid @RequestBody FeedItem feedItem,
            Principal principal) {
        log.debug("Creating feed item for user: {}", principal.getName());
        FeedItem createdItem = feedService.createFeedItem(feedItem, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    /**
     * Get trending items
     */
    @GetMapping("/trending")
    public ResponseEntity<List<FeedItem>> getTrendingItems(
            @RequestParam(defaultValue = "24") int hours) {
        log.debug("Fetching trending items for last {} hours", hours);
        List<FeedItem> trendingItems = feedService.getTrendingItems(hours);
        return ResponseEntity.ok(trendingItems);
    }

    /**
     * Get feed item by ID
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<FeedItem> getFeedItem(@PathVariable Long itemId) {
        log.debug("Fetching feed item with ID: {}", itemId);
        FeedItem item = feedService.getFeedItem(itemId);
        return ResponseEntity.ok(item);
    }

    /**
     * Like a feed item
     */
    @PostMapping("/items/{itemId}/like")
    public ResponseEntity<Void> likeFeedItem(
            @PathVariable Long itemId,
            Principal principal) {
        log.debug("User {} liking item {}", principal.getName(), itemId);
        feedService.likeFeedItem(itemId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Comment on a feed item
     */
    @PostMapping("/items/{itemId}/comments")
    public ResponseEntity<FeedItem> commentOnFeedItem(
            @PathVariable Long itemId,
            @RequestBody String comment,
            Principal principal) {
        log.debug("User {} commenting on item {}", principal.getName(), itemId);
        FeedItem updatedItem = feedService.addComment(itemId, comment, principal.getName());
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Get feed by category or tag
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<FeedItem>> getFeedByCategory(
            @PathVariable String category,
            Pageable pageable) {
        log.debug("Fetching feed for category: {}", category);
        Page<FeedItem> categoryFeed = feedService.getFeedByCategory(category, pageable);
        return ResponseEntity.ok(categoryFeed);
    }

    /**
     * Search feed items
     */
    @GetMapping("/search")
    public ResponseEntity<Page<FeedItem>> searchFeedItems(
            @RequestParam String query,
            Pageable pageable) {
        log.debug("Searching feed items with query: {}", query);
        Page<FeedItem> searchResults = feedService.searchFeedItems(query, pageable);
        return ResponseEntity.ok(searchResults);
    }
}
