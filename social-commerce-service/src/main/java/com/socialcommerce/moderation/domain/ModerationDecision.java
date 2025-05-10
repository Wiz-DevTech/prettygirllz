package com.socialcommerce.moderation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a moderation decision
 * Tracks approval/rejection status and reasoning
 */
@Data
@Entity
@Table(name = "moderation_decisions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @NotNull
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String contentSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModerationStatus status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "moderator_id")
    private Long moderatorId;

    @Column(name = "moderator_username")
    private String moderatorUsername;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @ElementCollection
    @CollectionTable(name = "moderation_flags", joinColumns = @JoinColumn(name = "decision_id"))
    @Column(name = "flag")
    private List<String> flags = new ArrayList<>();

    @Column(name = "is_automated")
    private Boolean isAutomated = false;

    @Column(name = "reported_by")
    private String reportedBy;

    @Column(name = "report_reason")
    private String reportReason;

    @OneToMany(mappedBy = "decision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ModerationAppeal> appeals = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * Nested appeal entity
     */
    @Entity
    @Table(name = "moderation_appeals")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModerationAppeal {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "decision_id")
        private ModerationDecision decision;

        @Column(name = "appellant_id", nullable = false)
        private Long appellantId;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String reason;

        @Enumerated(EnumType.STRING)
        private AppealStatus status = AppealStatus.PENDING;

        @Column(name = "reviewer_id")
        private Long reviewerId;

        @Column(name = "review_notes")
        private String reviewNotes;

        @CreationTimestamp
        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "resolved_at")
        private LocalDateTime resolvedAt;

        public enum AppealStatus {
            PENDING, APPROVED, REJECTED
        }
    }
}
