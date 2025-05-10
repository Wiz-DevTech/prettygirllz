package com.wizdevtech.identityaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Data transfer object for avatar configuration settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarConfigDTO {
    private String avatarType;
    private String faceShape;
    private String skinTone;
    private String hairStyle;
    private String hairColor;
    private String eyeColor;
    private String bodyType;
    private Double height;
    private String clothingStyle;
    private String animationDriver;
    private String renderingEngine;

    @Builder.Default
    private Map<String, Object> additionalSettings = new HashMap<>();

    /**
     * Helper method to add a single additional setting
     * @param key Setting key
     * @param value Setting value
     * @return This instance for method chaining
     */
    public AvatarConfigDTO addSetting(String key, Object value) {
        if (this.additionalSettings == null) {
            this.additionalSettings = new HashMap<>();
        }
        this.additionalSettings.put(key, value);
        return this;
    }
}