package com.wizdevtech.identityaccess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Avatar configuration options for advanced avatar customization.
 * This class is intended for future implementation of the detailed avatar
 * customization system and is not currently used in production code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarOptions {

    public enum AvatarType {
        CARTOON,
        REALISTIC,
        SUPER_REALISTIC,
        VIDEO_AVATAR
    }

    public enum GenderPreference {
        MALE,
        FEMALE,
        NON_BINARY,
        UNSPECIFIED
    }

    // Core Identity
    @NotBlank(message = "Avatar type is required")
    private AvatarType avatarType;

    @NotNull(message = "Gender preference is required")
    private GenderPreference genderPreference;

    // Facial Features
    @NotBlank(message = "Face shape is required")
    private String faceShape;  // e.g., "OVAL", "SQUARE"

    @NotBlank(message = "Skin tone is required")
    private String skinTone;   // e.g., "#FFDBAC", "LIGHT_MEDIUM"

    @NotBlank(message = "Hair style is required")
    private String hairStyle;  // e.g., "CURLY_LONG", "AFRO"

    private String hairColor;  // Hex code or preset ("BLONDE", "BLACK")

    @NotBlank(message = "Eye color is required")
    private String eyeColor;   // e.g., "BLUE", "#00FF00"

    // Advanced Configs (JSON)
    private String facialFeatures; // JSON: {"eyebrowThickness": 0.5, "noseWidth": 0.3}
    private String bodyShape;      // JSON: {"height": 1.75, "shoulderWidth": 0.9}
    private String clothingStyle;  // e.g., "BUSINESS_CASUAL", "FUTURISTIC"

    // Dynamic Extras
    private Map<String, Object> accessories;
    // e.g., {"glasses": "RECTANGULAR", "jewelry": ["NECKLACE", "WATCH"]}

    /**
     * Determines if the avatar is of a realistic type
     * @return true if the avatar is realistic or super-realistic
     */
    public boolean isRealisticType() {
        return avatarType == AvatarType.REALISTIC ||
                avatarType == AvatarType.SUPER_REALISTIC;
    }

    /**
     * Determines if high resolution rendering is required
     * @return true if the avatar requires high resolution rendering
     */
    public boolean requiresHighResolution() {
        return avatarType == AvatarType.SUPER_REALISTIC;
    }
}