package com.wizdevtech.identityaccess.controller;

import com.wizdevtech.identityaccess.dto.*;
import com.wizdevtech.identityaccess.exception.ResourceNotFoundException;
import com.wizdevtech.identityaccess.service.AvatarService;
import com.wizdevtech.identityaccess.service.AvatarService.AvatarServiceException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/avatars")
public class AvatarController {
    private static final Logger logger = LoggerFactory.getLogger(AvatarController.class);
    private final AvatarService avatarService;

    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserAvatar(@PathVariable Long userId) {
        try {
            logger.debug("Fetching avatar for user ID: {}", userId);
            AvatarResponse response = avatarService.getUserAvatar(userId);
            return ResponseEntity.ok()
                    .header("X-Avatar-Version", String.valueOf(response.getVersion()))
                    .body(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("Avatar not found for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Avatar not found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (AvatarServiceException e) {
            logger.error("Service error fetching avatar for user ID: {}", userId, e);
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse("Avatar service error", e.getMessage(), e.getStatus().value()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching avatar for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<?> previewAvatar(
            @Valid @RequestBody AvatarConfigDTO configDTO) {
        try {
            logger.debug("Generating preview with configuration: {}", configDTO);
            AvatarPreviewResponse preview = avatarService.generateTemporaryPreview(configDTO);
            return ResponseEntity.ok(preview);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid preview request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid request", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (AvatarServiceException e) {
            logger.error("Service error generating preview", e);
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse("Preview generation failed", e.getMessage(), e.getStatus().value()));
        } catch (Exception e) {
            logger.error("Unexpected error generating preview", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Failed to generate preview", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserAvatar(
            @PathVariable Long userId,
            @Valid @RequestBody AvatarUpdateRequest request) {
        try {
            logger.debug("Updating avatar for user ID: {} with request: {}", userId, request);
            AvatarResponse response = avatarService.updateUserAvatar(userId, request);
            return ResponseEntity.ok()
                    .header("X-Avatar-Version", String.valueOf(response.getVersion()))
                    .body(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for avatar update: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid avatar update request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid request", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (AvatarServiceException e) {
            logger.error("Service error updating avatar for user ID: {}", userId, e);
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse("Avatar update failed", e.getMessage(), e.getStatus().value()));
        } catch (Exception e) {
            logger.error("Unexpected error updating avatar for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Failed to update avatar", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping(value = "/user/{userId}/upload", consumes = "multipart/form-data")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadAvatarImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.debug("Uploading avatar image for user ID: {}", userId);
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Process file upload through service
            AvatarResponse response = avatarService.handleAvatarImageUpload(userId, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error uploading avatar image for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Upload failed", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    private static class ErrorResponse {
        private final String error;
        private final String message;
        private final int status;
        private final long timestamp = System.currentTimeMillis();

        public ErrorResponse(String error, String message, int status) {
            this.error = error;
            this.message = message;
            this.status = status;
        }

        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public int getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
    }

    private static class SuccessResponse {
        private final boolean success = true;
        private final String message;
        private final long timestamp = System.currentTimeMillis();

        public SuccessResponse(String message) {
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}