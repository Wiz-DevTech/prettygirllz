package com.socialcommerce.moderation.service;

import com.socialcommerce.moderation.domain.ModerationDecision;
import com.socialcommerce.moderation.domain.ModerationStatus;
import com.socialcommerce.moderation.repository.ModerationRepository;
import com.socialcommerce.moderation.rules.ContentRules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for content moderation logic
 * Implements automated and manual review workflows
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ModerationRepository moderationRepository;
    private final ContentRules contentRules;

    /**
     * Moderate content automatically
     */
    @Transactional
    public ModerationDecision moderateContent(Long contentId, String contentType, String content) {
        log.debug("Moderating content: {} of type {}", contentId, contentType);
        
        // Check if decision already exists
        ModerationDecision existingDecision = moderationRepository
            .findByContentIdAndContentType(contentId, contentType)
            .orElse(null);
            
        if (existingDecision != null) {
            return existingDecision;
        }
        
        // Apply automated moderation rules
        ModerationDecision decision = contentRules.analyzeContent(content);
        decision.setContentId(contentId);
        decision.setContentType(contentType);
        decision.setContentSnapshot(content.substring(0, Math.min(content.length(), 500)));
        decision.setIsAutomated(true);
        
        // If confidence is low, mark for manual review
        if (decision.getConfidenceScore() < 0.8) {
            decision.setStatus(ModerationStatus.UNDER_REVIEW);
        }
        
        ModerationDecision savedDecision = moderationRepository.save(decision);
        log.info("Content {} moderated with status: {}", contentId, savedDecision.getStatus());
        
        return savedDecision;
    }

    /**
     * Get moderation decision
     */
    @Transactional(readOnly = true)
    public ModerationDecision getDecision(Long contentId, String contentType) {
        return moderationRepository.findByContentIdAndContentType(contentId, contentType)
            .orElseThrow(() -> new RuntimeException("Moderation decision not found"));
    }

    /**
     * Get pending moderation items
     */
    @Transactional(readOnly = true)
    public Page<ModerationDecision> getPendingItems(Pageable pageable) {
        return moderationRepository.findByStatusOrderByCreatedAtAsc(
            ModerationStatus.UNDER_REVIEW, 
            pageable
        );
    }

    /**
     * Update moderation decision manually
     */
    @Transactional
    public ModerationDecision updateDecision(Long decisionId, ModerationStatus status, 
                                           String reason, String moderatorUsername) {
        ModerationDecision decision = moderationRepository.findById(decisionId)
            .orElseThrow(() -> new RuntimeException("Decision not found"));
            
        decision.setStatus(status);
        decision.setReason(reason);
        decision.setModeratorUsername(moderatorUsername);
        decision.setReviewedAt(LocalDateTime.now());
        decision.setIsAutomated(false);
        
        ModerationDecision updatedDecision = moderationRepository.save(decision);
        log.info("Decision {} updated by moderator {} to status: {}", 
                decisionId, moderatorUsername, status);
                
        return updatedDecision;
    }

    /**
     * Get moderation statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getModerationStats(String timeframe) {
        LocalDateTime since = calculateTimeframeSince(timeframe);
        List<Object[]> rawStats = moderationRepository.getModerationStats(since);
        
        Map<String, Object> stats = new HashMap<>();
        if (!rawStats.isEmpty()) {
            Object[] stat = rawStats.get(0);
            stats.put("totalDecisions", stat[0]);
            stats.put("approvedCount", stat[1]);
            stats.put("rejectedCount", stat[2]);
            stats.put("pendingCount", stat[3]);
            stats.put("avgConfidence", stat[4]);
        }
        
        // Add moderator metrics
        List<Object[]> moderatorMetrics = moderationRepository.getModeratorMetrics(since);
        stats.put("moderatorMetrics", moderatorMetrics);
        
        return stats;
    }

    /**
     * Report content
     */
    @Transactional
    public ModerationDecision reportContent(Long contentId, String contentType, 
                                          String reason, String reporterUsername) {
        // Check if already reported
        ModerationDecision existingDecision = moderationRepository
            .findByContentIdAndContentType(contentId, contentType)
            .orElse(null);
            
        if (existingDecision != null) {
            existingDecision.setStatus(ModerationStatus.FLAGGED);
            existingDecision.getFlags().add("USER_REPORTED");
            return moderationRepository.save(existingDecision);
        }
        
        // Create new decision for reported content
        ModerationDecision decision = ModerationDecision.builder()
            .contentId(contentId)
            .contentType(contentType)
            .status(ModerationStatus.FLAGGED)
            .reportedBy(reporterUsername)
            .reportReason(reason)
            .build();
            
        decision.getFlags().add("USER_REPORTED");
        
        return moderationRepository.save(decision);
    }

    /**
     * Appeal moderation decision
     */
    @Transactional
    public ModerationDecision appealDecision(Long decisionId, String reason, String appellantUsername) {
        ModerationDecision decision = moderationRepository.findById(decisionId)
            .orElseThrow(() -> new RuntimeException("Decision not found"));
            
        ModerationDecision.ModerationAppeal appeal = new ModerationDecision.ModerationAppeal();
        // appeal.setAppellantId(getUserIdFromUsername(appellantUsername));
        appeal.setReason(reason);
        appeal.setDecision(decision);
        
        decision.getAppeals().add(appeal);
        decision.setStatus(ModerationStatus.UNDER_REVIEW);
        
        return moderationRepository.save(decision);
    }

    /**
     * Calculate timeframe for statistics
     */
    private LocalDateTime calculateTimeframeSince(String timeframe) {
        if (timeframe == null) timeframe = "day";
        
        return switch (timeframe.toLowerCase()) {
            case "hour" -> LocalDateTime.now().minusHours(1);
            case "day" -> LocalDateTime.now().minusDays(1);
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            default -> LocalDateTime.now().minusDays(1);
        };
    }
}
