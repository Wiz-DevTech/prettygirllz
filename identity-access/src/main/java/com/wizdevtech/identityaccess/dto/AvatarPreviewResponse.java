package com.wizdevtech.identityaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for avatar preview generation
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarPreviewResponse {
    private String previewUrl;
    private AvatarConfigDTO config;

    // Optional fields for extended functionality
    @Builder.Default
    private Boolean success = true;
    private String message;
    private Long expiresAt;
    private String previewId;
}