package com.wizdevtech.identityaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for avatar information
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponse {
    private Long userId;
    private String avatarPreviewUrl;
    private String avatarType;
    private AvatarConfigDTO config;
    private Integer version;

    // Additional fields for extended functionality
    @Builder.Default
    private Boolean success = true;
    private String message;
    private String renderUrl;
}