package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.config.AvatarEngineConfig;
import com.wizdevtech.identityaccess.model.AvatarConfig;
import com.wizdevtech.identityaccess.model.enums.AvatarType;
import com.wizdevtech.identityaccess.model.enums.RenderingEngine;
import com.wizdevtech.identityaccess.service.render.UnityRenderEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service responsible for rendering avatars and generating previews
 * using the appropriate rendering engines based on configuration.
 */
@Service
public class AvatarRenderService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarRenderService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String DEFAULT_PREVIEW_PATH = "default.png";

    private final AvatarEngineConfig avatarEngineConfig;
    private final UnityRenderEngine unityRenderEngine;

    @Autowired
    public AvatarRenderService(
            AvatarEngineConfig avatarEngineConfig,
            UnityRenderEngine unityRenderEngine) {
        this.avatarEngineConfig = avatarEngineConfig;
        this.unityRenderEngine = unityRenderEngine;
    }

    /**
     * Initialize the service and create required directories
     */
    @PostConstruct
    public void init() {
        try {
            // Ensure the avatar storage directory exists
            String storagePath = getStoragePath();
            Path directory = Paths.get(storagePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                logger.info("Created avatar storage directory: {}", storagePath);
            }

            // Create default avatar if it doesn't exist
            Path defaultPath = Paths.get(storagePath, DEFAULT_PREVIEW_PATH);
            if (!Files.exists(defaultPath)) {
                File file = defaultPath.toFile();
                file.createNewFile();
                logger.info("Created empty default avatar image at: {}", defaultPath);
            }
        } catch (Exception e) {
            logger.error("Error initializing AvatarRenderService: {}", e.getMessage(), e);
        }
    }

    /**
     * Generates a preview for an avatar configuration
     * @param config The avatar configuration to render
     * @return URL to the generated preview
     */
    public String generatePreview(AvatarConfig config) {
        String filename = generateAvatarFilename(config);
        logger.info("Generating preview for avatar configuration: {}", filename);

        try {
            // In a real implementation, this would call the actual rendering engine
            if (config.getRenderingEngine() != null && config.getRenderingEngine() == RenderingEngine.UNITY) {
                // Use Unity render engine if available
                String storagePath = getStoragePath();
                String outputPath = Paths.get(storagePath, filename).toString();

                // Call the Unity render engine
                unityRenderEngine.renderAvatar(
                        outputPath,
                        config.getAvatarType().name(),
                        config.getAdditionalSettings() != null ? config.getAdditionalSettings() : "{}"
                );
            }

            // Return the URL to access the avatar
            return getAvatarUrl(filename);
        } catch (Exception e) {
            logger.error("Error generating preview: {}", e.getMessage(), e);
            return getAvatarUrl(DEFAULT_PREVIEW_PATH);
        }
    }

    /**
     * Generates a default preview
     * @return URL to the default preview
     */
    public String generateDefaultPreview() {
        return getAvatarUrl(DEFAULT_PREVIEW_PATH);
    }

    /**
     * Generate a unique filename for an avatar based on its configuration
     * @param config Avatar configuration
     * @return Unique filename
     */
    private String generateAvatarFilename(AvatarConfig config) {
        // Create a unique filename based on configuration
        String type = config.getAvatarType().toString().toLowerCase();

        // Use first characters of properties to create a simple identifier
        String skinTone = config.getSkinTone() != null ?
                config.getSkinTone().substring(0, Math.min(3, config.getSkinTone().length())) : "def";

        String hairStyle = config.getHairStyle() != null ?
                config.getHairStyle().substring(0, Math.min(3, config.getHairStyle().length())) : "def";

        // Add timestamp and unique ID for better uniqueness
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);

        return type + "_" + skinTone + "_" + hairStyle + "_" + timestamp + "_" + uniqueId + ".png";
    }

    /**
     * Get the physical storage path from the configuration
     * @return Storage path as string
     */
    private String getStoragePath() {
        String path = avatarEngineConfig.getAvatarStoragePath();
        // Remove 'file:' prefix if present
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        return path;
    }

    /**
     * Get the URL to an avatar file
     * @param filename The avatar filename
     * @return URL to access the avatar
     */
    private String getAvatarUrl(String filename) {
        String staticPath = avatarEngineConfig.getAvatarStaticPath();
        // Ensure path ends with a slash
        if (!staticPath.endsWith("/")) {
            staticPath += "/";
        }
        return staticPath + filename;
    }

    /**
     * Get the maximum allowed avatar file size from configuration
     * @return Maximum file size in bytes
     */
    public long getMaxAvatarFileSize() {
        return avatarEngineConfig.getMaxAvatarFileSize();
    }

    /**
     * Get the default avatar type from configuration
     * @return Default avatar type as enum
     */
    public AvatarType getDefaultAvatarType() {
        try {
            return AvatarType.valueOf(avatarEngineConfig.getDefaultAvatarType());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid default avatar type in config: {}", avatarEngineConfig.getDefaultAvatarType());
            return AvatarType.CARTOON; // Fallback
        }
    }

    /**
     * Get the default rendering engine from configuration
     * @return Default rendering engine as enum
     */
    public RenderingEngine getDefaultRenderEngine() {
        try {
            return RenderingEngine.valueOf(avatarEngineConfig.getDefaultRenderEngine());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid default render engine in config: {}", avatarEngineConfig.getDefaultRenderEngine());
            return RenderingEngine.UNITY; // Fallback
        }
    }
}