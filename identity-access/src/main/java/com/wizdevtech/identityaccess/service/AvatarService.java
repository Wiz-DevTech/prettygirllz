package com.wizdevtech.identityaccess.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizdevtech.identityaccess.config.AvatarEngineConfig;
import com.wizdevtech.identityaccess.dto.*;
import com.wizdevtech.identityaccess.exception.ResourceNotFoundException;
import com.wizdevtech.identityaccess.model.AvatarConfig;
import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.model.enums.AnimationDriver;
import com.wizdevtech.identityaccess.model.enums.AvatarType;
import com.wizdevtech.identityaccess.model.enums.RenderingEngine;
import com.wizdevtech.identityaccess.repository.AvatarConfigRepository;
import com.wizdevtech.identityaccess.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final UserRepository userRepository;
    private final AvatarConfigRepository avatarConfigRepository;
    private final AvatarRenderService avatarRenderService;
    private final AvatarEngineConfig avatarEngineConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public AvatarService(UserRepository userRepository,
                         AvatarConfigRepository avatarConfigRepository,
                         AvatarRenderService avatarRenderService,
                         AvatarEngineConfig avatarEngineConfig,
                         ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.avatarConfigRepository = avatarConfigRepository;
        this.avatarRenderService = avatarRenderService;
        this.avatarEngineConfig = avatarEngineConfig;
        this.objectMapper = objectMapper;
    }

    public AvatarResponse getUserAvatar(Long userId) {
        try {
            logger.debug("Fetching avatar for user ID: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new ResourceNotFoundException("User not found with ID: " + userId);
                    });

            Optional<AvatarConfig> configOptional = avatarConfigRepository.findByUserId(userId);
            AvatarConfig config = configOptional.orElseGet(() -> {
                logger.info("Creating default avatar config for user ID: {}", userId);
                return createDefaultAvatarConfig(user);
            });

            logger.debug("Successfully retrieved avatar for user ID: {}", userId);
            return mapToAvatarResponse(config);
        } catch (Exception e) {
            logger.error("Failed to get avatar for user ID: {}", userId, e);
            throw new AvatarServiceException("Failed to retrieve avatar", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AvatarResponse updateUserAvatar(Long userId, AvatarUpdateRequest request) {
        try {
            logger.debug("Updating avatar for user ID: {} with request: {}", userId, request);

            validateAvatarUpdateRequest(request);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new ResourceNotFoundException("User not found with ID: " + userId);
                    });

            AvatarConfig config = avatarConfigRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        logger.info("Creating new avatar config for user ID: {}", userId);
                        AvatarConfig newConfig = new AvatarConfig();
                        newConfig.setUser(user);
                        return newConfig;
                    });

            updateConfigFromDTO(config, request.getConfig());
            config.setVersion(config.getVersion() + 1);
            config.setUpdatedAt(LocalDateTime.now());

            String previewUrl = avatarRenderService.generatePreview(config);
            config.setAvatarPreviewUrl(previewUrl);

            AvatarConfig savedConfig = avatarConfigRepository.save(config);
            logger.info("Successfully updated avatar for user ID: {}", userId);

            return mapToAvatarResponse(savedConfig);
        } catch (Exception e) {
            logger.error("Failed to update avatar for user ID: {}", userId, e);
            throw new AvatarServiceException("Failed to update avatar", e,
                    e instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AvatarPreviewResponse generateTemporaryPreview(AvatarConfigDTO configDTO) {
        try {
            logger.debug("Generating temporary preview with config: {}", configDTO);

            validateAvatarConfigDTO(configDTO);

            AvatarConfig tempConfig = new AvatarConfig();
            updateConfigFromDTO(tempConfig, configDTO);

            tempConfig.setVersion(1);
            tempConfig.setAvatarType(configDTO.getAvatarType() != null
                    ? AvatarType.valueOf(configDTO.getAvatarType())
                    : AvatarType.valueOf(avatarEngineConfig.getDefaultAvatarType()));

            String previewUrl = avatarRenderService.generatePreview(tempConfig);
            logger.debug("Successfully generated temporary preview");

            return AvatarPreviewResponse.builder()
                    .previewUrl(previewUrl)
                    .config(configDTO)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to generate temporary preview", e);
            throw new AvatarServiceException("Failed to generate preview", e,
                    e instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateAvatarUpdateRequest(AvatarUpdateRequest request) {
        if (request == null || request.getConfig() == null) {
            throw new IllegalArgumentException("Avatar configuration is required");
        }
        validateAvatarConfigDTO(request.getConfig());
    }

    private void validateAvatarConfigDTO(AvatarConfigDTO configDTO) {
        if (configDTO == null) {
            throw new IllegalArgumentException("Avatar configuration cannot be null");
        }

        if (configDTO.getAvatarType() != null) {
            try {
                AvatarType.valueOf(configDTO.getAvatarType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid avatar type: " + configDTO.getAvatarType());
            }
        }

        if (configDTO.getRenderingEngine() != null) {
            try {
                RenderingEngine.valueOf(configDTO.getRenderingEngine());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid rendering engine: " + configDTO.getRenderingEngine());
            }
        }
    }

    private AvatarConfig createDefaultAvatarConfig(User user) {
        AvatarConfig config = new AvatarConfig();
        config.setUser(user);
        config.setAvatarType(AvatarType.valueOf(avatarEngineConfig.getDefaultAvatarType()));
        config.setAnimationDriver(AnimationDriver.VOICE);
        config.setRenderingEngine(RenderingEngine.valueOf(avatarEngineConfig.getDefaultRenderEngine()));
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        String previewUrl = avatarRenderService.generateDefaultPreview();
        config.setAvatarPreviewUrl(previewUrl);

        return avatarConfigRepository.save(config);
    }

    private void updateConfigFromDTO(AvatarConfig config, AvatarConfigDTO dto) {
        if (dto.getAvatarType() != null) {
            config.setAvatarType(AvatarType.valueOf(dto.getAvatarType()));
        }
        if (dto.getFaceShape() != null) config.setFaceShape(dto.getFaceShape());
        if (dto.getSkinTone() != null) config.setSkinTone(dto.getSkinTone());
        if (dto.getHairStyle() != null) config.setHairStyle(dto.getHairStyle());
        if (dto.getHairColor() != null) config.setHairColor(dto.getHairColor());
        if (dto.getEyeColor() != null) config.setEyeColor(dto.getEyeColor());
        if (dto.getBodyType() != null) config.setBodyType(dto.getBodyType());
        if (dto.getHeight() != null) config.setHeight(dto.getHeight());
        if (dto.getClothingStyle() != null) config.setClothingStyle(dto.getClothingStyle());
        if (dto.getAnimationDriver() != null) {
            config.setAnimationDriver(AnimationDriver.valueOf(dto.getAnimationDriver()));
        }
        if (dto.getRenderingEngine() != null) {
            config.setRenderingEngine(RenderingEngine.valueOf(dto.getRenderingEngine()));
        }

        if (dto.getAdditionalSettings() != null) {
            try {
                config.setAdditionalSettings(objectMapper.writeValueAsString(dto.getAdditionalSettings()));
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize additional settings", e);
                throw new AvatarServiceException("Invalid additional settings format", e, HttpStatus.BAD_REQUEST);
            }
        }
    }

    private AvatarResponse mapToAvatarResponse(AvatarConfig config) {
        AvatarConfigDTO configDTO = new AvatarConfigDTO();
        if (config.getAvatarType() != null) configDTO.setAvatarType(config.getAvatarType().name());
        configDTO.setFaceShape(config.getFaceShape());
        configDTO.setSkinTone(config.getSkinTone());
        configDTO.setHairStyle(config.getHairStyle());
        configDTO.setHairColor(config.getHairColor());
        configDTO.setEyeColor(config.getEyeColor());
        configDTO.setBodyType(config.getBodyType());
        configDTO.setHeight(config.getHeight());
        configDTO.setClothingStyle(config.getClothingStyle());
        if (config.getAnimationDriver() != null)
            configDTO.setAnimationDriver(config.getAnimationDriver().name());
        if (config.getRenderingEngine() != null)
            configDTO.setRenderingEngine(config.getRenderingEngine().name());

        if (config.getAdditionalSettings() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> additionalSettings = objectMapper.readValue(
                        config.getAdditionalSettings(), Map.class);
                configDTO.setAdditionalSettings(additionalSettings);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse additional settings for config ID: {}", config.getId(), e);
            }
        }

        return AvatarResponse.builder()
                .userId(config.getUser().getId())
                .avatarPreviewUrl(config.getAvatarPreviewUrl())
                .avatarType(config.getAvatarType() != null ? config.getAvatarType().name() : null)
                .config(configDTO)
                .version(config.getVersion())
                .build();
    }

    public static class AvatarServiceException extends RuntimeException {
        private final HttpStatus status;

        public AvatarServiceException(String message, Throwable cause, HttpStatus status) {
            super(message, cause);
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }
}