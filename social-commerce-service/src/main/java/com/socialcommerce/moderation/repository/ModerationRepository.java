package com.socialcommerce.moderation.repository;

import com.socialcommerce.moderation.domain.ModerationDecision;
import com.socialcommerce.moderation.domain.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for moderation decision persistence
 * Provides queries for content review workflow
 */
@Repository
public interface ModerationRepository extends JpaRepository<ModerationDecision, Long> {

    /**
     * Find decision by content ID and type
     */
    Optional<ModerationDecision> findByContentIdAndContentType(Long contentId, String contentType);

    /**
     * Find pending moderation items
     */
    Page<ModerationDecision> findByStatusOrderByCreatedAtAsc(ModerationStatus status, Pageable pageable);

    /**
     * Find decisions by moderator
     */
    Page<ModerationDecision> findByModeratorUsernameOrderByReviewedAtDesc(String moderatorUsername, Pageable pageable);

    /**
     * Find recent decisions
     */
    List<ModerationDecision> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after);

    /**
     * Count decisions by status
     */
    long countByStatus(ModerationStatus status);

    /**
     * Find automated decisions needing review
     */
    @Query("SELECT d FROM ModerationDecision d WHERE d.isAutomated = true " +
           "AND d.confidenceScore < :threshold AND d.status = 'PENDING'")
    Page<ModerationDecision> findLowConfidenceAutomatedDecisions(
        @Param("threshold") Double threshold, 
        Pageable pageable
    );

    /**
     * Get moderation statistics
     */
    @Query("SELECT " +
           "COUNT(d) as totalDecisions, " +
           "SUM(CASE WHEN d.status = 'APPROVED' THEN 1 ELSE 0 END) as approvedCount, " +
           "SUM(CASE WHEN d.status = 'REJECTED' THEN 1 ELSE 0 END) as rejectedCount, " +
           "SUM(CASE WHEN d.status = 'PENDING' THEN 1 ELSE 0 END) as pendingCount, " +
           "AVG(CASE WHEN d.isAutomated = true THEN d.confidenceScore ELSE NULL END) as avgConfidence " +
           "FROM ModerationDecision d WHERE d.createdAt > :since")
    List<Object[]> getModerationStats(@Param("since") LocalDateTime since);

    /**
     * Find flagged content by specific flag
     */
    @Query("SELECT d FROM ModerationDecision d JOIN d.flags f WHERE f = :flag")
    List<ModerationDecision> findByFlag(@Param("flag") String flag);

    /**
     * Find decisions with appeals
     */
    @Query("SELECT DISTINCT d FROM ModerationDecision d JOIN d.appeals a WHERE a.status = 'PENDING'")
    Page<ModerationDecision> findDecisionsWithPendingAppeals(Pageable pageable);

    /**
     * Get moderator performance metrics
     */
    @Query("SELECT d.moderatorUsername, " +
           "COUNT(d) as totalReviews, " +
           "AVG(EXTRACT(EPOCH FROM (d.reviewedAt - d.createdAt))) as avgReviewTime " +
           "FROM ModerationDecision d " +
           "WHERE d.moderatorUsername IS NOT NULL AND d.reviewedAt > :since " +
           "GROUP BY d.moderatorUsername")
    List<Object[]> getModeratorMetrics(@Param("since") LocalDateTime since);
}
