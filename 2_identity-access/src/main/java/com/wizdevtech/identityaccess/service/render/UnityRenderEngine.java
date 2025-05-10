package com.wizdevtech.identityaccess.service.render;

import com.wizdevtech.identityaccess.config.AvatarEngineConfig;
import com.wizdevtech.identityaccess.model.AvatarConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of rendering engine that uses Unity for avatar rendering
 */
@Component
public class UnityRenderEngine implements RenderEngine {
    private static final Logger logger = LoggerFactory.getLogger(UnityRenderEngine.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String DEFAULT_ANIMATION_PATH = "default.gif";

    private AvatarEngineConfig avatarEngineConfig;

    @Autowired
    public UnityRenderEngine(AvatarEngineConfig avatarEngineConfig) {
        this.avatarEngineConfig = avatarEngineConfig;
    }

    /**
     * No-args constructor for when used directly in RenderEngineFactory
     */
    public UnityRenderEngine() {
        // This will be used when created through RenderEngineFactory
        // avatarEngineConfig will be null, and methods will use default values
    }

    @Override
    public String renderPreview(AvatarConfig config) {
        logger.info("Rendering preview with Unity engine for avatar type: {}",
                config.getAvatarType() != null ? config.getAvatarType() : "null");

        try {
            // Create a unique filename
            String filename = generateAvatarFilename(config);

            // If we have access to config, use it to determine paths
            if (avatarEngineConfig != null) {
                String outputPath = getOutputPath(filename);

                // Generate the actual file
                generatePreviewFile(outputPath, config);

                // Return the URL path
                return getAvatarUrl(filename);
            } else {
                // Fall back to the original implementation if no config is available
                return "/static/avatars/" + filename;
            }
        } catch (Exception e) {
            logger.error("Error rendering preview: {}", e.getMessage(), e);
            return getDefaultPreviewUrl();
        }
    }

    @Override
    public String renderAnimation(AvatarConfig config, String animationParams) {
        logger.info("Rendering animation with Unity engine for avatar type: {}",
                config.getAvatarType() != null ? config.getAvatarType() : "null");

        try {
            // In a real implementation, this would generate an animated version
            // For now, we'll just return a static URL
            if (avatarEngineConfig != null) {
                return avatarEngineConfig.getAvatarStaticPath() + "animations/" + DEFAULT_ANIMATION_PATH;
            } else {
                return "/static/animations/" + DEFAULT_ANIMATION_PATH;
            }
        } catch (Exception e) {
            logger.error("Error rendering animation: {}", e.getMessage(), e);
            return "/static/animations/" + DEFAULT_ANIMATION_PATH;
        }
    }

    /**
     * Renders an avatar and saves it to a specified path
     * @param outputPath Path to save the rendered avatar
     * @param avatarType Type of avatar to render
     * @param settings JSON string with rendering settings
     * @return Path to the rendered avatar file
     */
    public String renderAvatar(String outputPath, String avatarType, String settings) {
        logger.info("Rendering avatar with Unity engine: type={}, outputPath={}", avatarType, outputPath);

        try {
            // In a real implementation, this would call Unity's rendering API
            // For now, just create an empty file
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    logger.warn("Failed to create directory: {}", parentDir.getAbsolutePath());
                }
            }

            if (!outputFile.exists()) {
                boolean fileCreated = outputFile.createNewFile();
                if (!fileCreated) {
                    logger.warn("Failed to create file: {}", outputFile.getAbsolutePath());
                }
            } else {
                // Touch the file to update modified timestamp
                boolean timestampUpdated = outputFile.setLastModified(System.currentTimeMillis());
                if (!timestampUpdated) {
                    logger.warn("Failed to update file timestamp: {}", outputFile.getAbsolutePath());
                }
            }

            // Write some placeholder data
            Files.write(Paths.get(outputPath), ("Unity Render - Type: " + avatarType + "\nSettings: " + settings).getBytes());

            return outputPath;
        } catch (IOException e) {
            logger.error("Error rendering avatar: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate an avatar preview file based on configuration
     * @param outputPath Path to save the file
     * @param config Avatar configuration
     * @throws IOException If file operations fail
     */
    private void generatePreviewFile(String outputPath, AvatarConfig config) throws IOException {
        // In a real implementation, this would call Unity's rendering API
        // For now, we're just creating a file with some basic content
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean dirCreated = parentDir.mkdirs();
            if (!dirCreated) {
                logger.warn("Failed to create directory: {}", parentDir.getAbsolutePath());
            }
        }

        // Create a text representation of the avatar parameters for demonstration
        String content = String.format(
                "Unity Render Engine Preview\n" +
                        "Avatar Type: %s\n" +
                        "Face Shape: %s\n" +
                        "Skin Tone: %s\n" +
                        "Hair Style: %s\n" +
                        "Hair Color: %s\n" +
                        "Eye Color: %s\n" +
                        "Body Type: %s\n" +
                        "Generated: %s",
                config.getAvatarType(),
                config.getFaceShape(),
                config.getSkinTone(),
                config.getHairStyle(),
                config.getHairColor(),
                config.getEyeColor(),
                config.getBodyType(),
                LocalDateTime.now()
        );

        // Write the content to the file
        Files.write(Paths.get(outputPath), content.getBytes());
    }

    /**
     * Generate a filename based on avatar configuration
     * @param config Avatar configuration
     * @return Unique filename
     */
    private String generateAvatarFilename(AvatarConfig config) {
        // Create a unique filename based on configuration
        String type = config.getAvatarType() != null ?
                config.getAvatarType().toString().toLowerCase() : "default";

        String skinTone = config.getSkinTone() != null ?
                config.getSkinTone().substring(0, Math.min(3, config.getSkinTone().length())).toLowerCase() : "def";

        String hairStyle = config.getHairStyle() != null ?
                config.getHairStyle().substring(0, Math.min(3, config.getHairStyle().length())).toLowerCase() : "def";

        // Add timestamp and unique ID for better uniqueness
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);

        return type + "_" + skinTone + "_" + hairStyle + "_" + timestamp + "_" + uniqueId + ".png";
    }

    /**
     * Get the output path for a file
     * @param filename Filename to append to the path
     * @return Full output path
     */
    private String getOutputPath(String filename) {
        if (avatarEngineConfig == null) {
            // Default path if config is not available
            return "./avatars/" + filename;
        }

        String storagePath = avatarEngineConfig.getAvatarStoragePath();
        // Remove 'file:' prefix if present
        if (storagePath.startsWith("file:")) {
            storagePath = storagePath.substring(5);
        }

        return Paths.get(storagePath, filename).toString();
    }

    /**
     * Get the URL to access an avatar file
     * @param filename Filename to append to the URL
     * @return URL path
     */
    private String getAvatarUrl(String filename) {
        if (avatarEngineConfig == null) {
            // Default URL if config is not available
            return "/static/avatars/" + filename;
        }

        String staticPath = avatarEngineConfig.getAvatarStaticPath();
        // Ensure path ends with a slash
        if (!staticPath.endsWith("/")) {
            staticPath += "/";
        }

        return staticPath + filename;
    }

    /**
     * Get the default preview URL
     * @return Default preview URL
     */
    private String getDefaultPreviewUrl() {
        if (avatarEngineConfig == null) {
            return "/static/avatars/default.png";
        }

        return getAvatarUrl("default.png");
    }
}