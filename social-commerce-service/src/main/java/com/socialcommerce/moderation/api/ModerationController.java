package com.socialcommerce.moderation.api;

import com.socialcommerce.moderation.domain.ModerationDecision;
import com.socialcommerce.moderation.domain.ModerationStatus;
import com.socialcommerce.moderation.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

/**
 * REST API Controller for content moderation
 * Manages automated and manual content review processes
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    /**
     * Submit content for moderation
     */
    @PostMapping("/submit")
    public ResponseEntity<ModerationDecision> submitForModeration(
            @Valid @RequestBody ModerationRequest request) {
        log.debug("Submitting content for moderation: {}", request.getContentId());
        ModerationDecision decision = moderationService.moderateContent(
            request.getContentId(), 
            request.getContentType(), 
            request.getContent()
        );
        return ResponseEntity.ok(decision);
    }

    /**
     * Get moderation decision for content
     */
    @GetMapping("/decisions/{contentId}")
    public ResponseEntity<ModerationDecision> getModerationDecision(
            @PathVariable Long contentId,
            @RequestParam String contentType) {
        log.debug("Fetching moderation decision for content: {}", contentId);
        ModerationDecision decision = moderationService.getDecision(contentId, contentType);
        return ResponseEntity.ok(decision);
    }

    /**
     * Get pending moderation items (for moderators)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Page<ModerationDecision>> getPendingItems(Pageable pageable) {
        log.debug("Fetching pending moderation items");
        Page<ModerationDecision> pendingItems = moderationService.getPendingItems(pageable);
        return ResponseEntity.ok(pendingItems);
    }

    /**
     * Manual moderation decision
     */
    @PutMapping("/decisions/{decisionId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ModerationDecision> updateDecision(
            @PathVariable Long decisionId,
            @RequestBody ModerationUpdate update,
            Principal principal) {
        log.debug("Moderator {} updating decision {}", principal.getName(), decisionId);
        ModerationDecision updatedDecision = moderationService.updateDecision(
            decisionId, 
            update.getStatus(), 
            update.getReason(), 
            principal.getName()
        );
        return ResponseEntity.ok(updatedDecision);
    }

    /**
     * Get moderation statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getModerationStats(
            @RequestParam(required = false) String timeframe) {
        log.debug("Fetching moderation statistics");
        Map<String, Object> stats = moderationService.getModerationStats(timeframe);
        return ResponseEntity.ok(stats);
    }

    /**
     * Report content
     */
    @PostMapping("/report")
    public ResponseEntity<ModerationDecision> reportContent(
            @Valid @RequestBody ReportRequest reportRequest,
            Principal principal) {
        log.debug("User {} reporting content {}", principal.getName(), reportRequest.getContentId());
        ModerationDecision decision = moderationService.reportContent(
            reportRequest.getContentId(),
            reportRequest.getContentType(),
            reportRequest.getReason(),
            principal.getName()
        );
        return ResponseEntity.ok(decision);
    }

    /**
     * Appeal moderation decision
     */
    @PostMapping("/appeals")
    public ResponseEntity<ModerationDecision> appealDecision(
            @Valid @RequestBody AppealRequest appealRequest,
            Principal principal) {
        log.debug("User {} appealing decision {}", principal.getName(), appealRequest.getDecisionId());
        ModerationDecision appealResult = moderationService.appealDecision(
            appealRequest.getDecisionId(),
            appealRequest.getReason(),
            principal.getName()
        );
        return ResponseEntity.ok(appealResult);
    }

    /**
     * DTO classes for requests
     */
    @lombok.Data
    public static class ModerationRequest {
        private Long contentId;
        private String contentType;
        private String content;
    }

    @lombok.Data
    public static class ModerationUpdate {
        private ModerationStatus status;
        private String reason;
    }

    @lombok.Data
    public static class ReportRequest {
        private Long contentId;
        private String contentType;
        private String reason;
    }

    @lombok.Data
    public static class AppealRequest {
        private Long decisionId;
        private String reason;
    }
}
