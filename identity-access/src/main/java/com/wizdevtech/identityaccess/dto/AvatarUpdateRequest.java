package com.wizdevtech.identityaccess.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for updating avatar configuration
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AvatarUpdateRequest {
    @NotNull(message = "Avatar configuration is required")
    @Valid
    private AvatarConfigDTO config;

    // Optional field for preview ID reference
    private String previewId;
}