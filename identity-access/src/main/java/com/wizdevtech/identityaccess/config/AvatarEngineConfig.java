package com.wizdevtech.identityaccess.config;

import com.wizdevtech.identityaccess.service.render.UnityRenderEngine;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for avatar rendering and storage settings
 */
@Configuration
@Getter // Lombok will automatically generate getters for all fields
public class AvatarEngineConfig implements WebMvcConfigurer {

    @Value("${avatar.static.path:/static/avatars/}")
    private String avatarStaticPath;

    @Value("${avatar.storage.path:file:./avatars/}")
    private String avatarStoragePath;

    @Value("${avatar.max.size:5242880}") // Default 5MB
    private long maxAvatarFileSize;

    @Value("${avatar.default.type:CARTOON}")
    private String defaultAvatarType;

    @Value("${avatar.default.engine:UNITY}")
    private String defaultRenderEngine;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map avatar static path to physical location
        registry.addResourceHandler(avatarStaticPath + "**")
                .addResourceLocations(avatarStoragePath);
    }

    /**
     * Creates the Unity rendering engine bean with this configuration
     * @return Configured UnityRenderEngine instance
     */
    @Bean(name = "configuredUnityRenderEngine")
    public UnityRenderEngine unityRenderEngine() {
        return new UnityRenderEngine(this);
    }
}