package com.socialcommerce.moderation.rules;

import com.socialcommerce.moderation.domain.ModerationDecision;
import com.socialcommerce.moderation.domain.ModerationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Content moderation rules and filters
 * Defines prohibited content patterns and policies
 */
@Slf4j
@Component
public class ContentRules {

    private static final List<String> PROHIBITED_WORDS = Arrays.asList(
        // Add actual prohibited words here
        "spam", "scam", "fake"
    );

    private static final List<Pattern> SUSPICIOUS_PATTERNS = Arrays.asList(
        Pattern.compile("\\b(buy|sell)\\s+now\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b\\d{3,}%\\s+off\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(click|visit)\\s+here\\b", Pattern.CASE_INSENSITIVE)
    );

    private static final List<Pattern> CONTACT_PATTERNS = Arrays.asList(
        Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"),
        Pattern.compile("\\b(whatsapp|telegram|wechat)\\b", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Analyze content and return moderation decision
     */
    public ModerationDecision analyzeContent(String content) {
        ModerationDecision decision = new ModerationDecision();
        List<String> flags = new ArrayList<>();
        double confidenceScore = 1.0;
        
        // Check for prohibited words
        if (containsProhibitedWords(content)) {
            flags.add("PROHIBITED_WORDS");
            confidenceScore -= 0.3;
        }
        
        // Check for suspicious patterns
        if (containsSuspiciousPatterns(content)) {
            flags.add("SUSPICIOUS_PATTERN");
            confidenceScore -= 0.2;
        }
        
        // Check for contact information
        if (containsContactInfo(content)) {
            flags.add("CONTACT_INFO");
            confidenceScore -= 0.2;
        }
        
        // Check content length
        if (content.length() < 10) {
            flags.add("TOO_SHORT");
            confidenceScore -= 0.1;
        }
        
        // Check for excessive capitalization
        if (isExcessivelyCapitalized(content)) {
            flags.add("EXCESSIVE_CAPS");
            confidenceScore -= 0.1;
        }
        
        // Check for excessive repetition
        if (hasExcessiveRepetition(content)) {
            flags.add("REPETITIVE");
            confidenceScore -= 0.1;
        }
        
        // Determine status based on flags and confidence
        ModerationStatus status;
        String reason = "";
        
        if (flags.contains("PROHIBITED_WORDS")) {
            status = ModerationStatus.REJECTED;
            reason = "Content contains prohibited words";
        } else if (confidenceScore < 0.5) {
            status = ModerationStatus.UNDER_REVIEW;
            reason = "Content flagged for manual review";
        } else if (flags.isEmpty()) {
            status = ModerationStatus.APPROVED;
            reason = "Content passed automated checks";
        } else {
            status = ModerationStatus.UNDER_REVIEW;
            reason = "Content requires manual review";
        }
        
        decision.setStatus(status);
        decision.setReason(reason);
        decision.setConfidenceScore(Math.max(0, confidenceScore));
        decision.setFlags(flags);
        
        log.debug("Content analyzed - Status: {}, Confidence: {}, Flags: {}", 
                 status, confidenceScore, flags);
        
        return decision;
    }

    private boolean containsProhibitedWords(String content) {
        String lowerContent = content.toLowerCase();
        return PROHIBITED_WORDS.stream()
            .anyMatch(word -> lowerContent.contains(word.toLowerCase()));
    }

    private boolean containsSuspiciousPatterns(String content) {
        return SUSPICIOUS_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(content).find());
    }

    private boolean containsContactInfo(String content) {
        return CONTACT_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(content).find());
    }

    private boolean isExcessivelyCapitalized(String content) {
        if (content.length() < 10) return false;
        
        long uppercaseCount = content.chars()
            .filter(Character::isUpperCase)
            .count();
            
        double uppercaseRatio = (double) uppercaseCount / content.length();
        return uppercaseRatio > 0.5;
    }

    private boolean hasExcessiveRepetition(String content) {
        String[] words = content.split("\\s+");
        if (words.length < 5) return false;
        
        for (int i = 0; i < words.length - 2; i++) {
            if (words[i].equals(words[i + 1]) && words[i].equals(words[i + 2])) {
                return true;
            }
        }
        
        return false;
    }
}
