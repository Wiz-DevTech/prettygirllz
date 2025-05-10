package com.wizdevtech.identityaccess.controller;

import com.wizdevtech.identityaccess.dto.AuthenticationRequest;
import com.wizdevtech.identityaccess.dto.AuthenticationResponse;
import com.wizdevtech.identityaccess.dto.RegistrationRequest;
import com.wizdevtech.identityaccess.exception.ResourceNotFoundException;
import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.service.AuthenticationService;
import com.wizdevtech.identityaccess.service.JwtService;
import com.wizdevtech.identityaccess.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/users")
    public ModelAndView showAuthUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        ModelAndView modelAndView = new ModelAndView("auth-admin");
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = authService.findUsers(search, pageable);

        modelAndView.addObject("users", userPage.getContent());
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", userPage.getTotalPages());
        modelAndView.addObject("searchTerm", search);
        modelAndView.addObject("availableRoles", Set.of("ROLE_USER", "ROLE_ADMIN"));

        return modelAndView;
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            logger.debug("Fetching user by ID: {}", id);
            User user = userService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Error fetching user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/users/{userId}/avatar")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getAvatar(@PathVariable Long userId) {
        try {
            logger.debug("Fetching avatar for user ID: {}", userId);

            if (!userService.existsById(userId)) {
                logger.warn("User not found: {}", userId);
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }

            byte[] avatar = userService.getUserAvatar(userId);
            MediaType mediaType = userService.getAvatarMediaType(userId);

            logger.debug("Avatar fetched successfully for user: {}", userId);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                    .body(avatar);
        } catch (ResourceNotFoundException e) {
            logger.warn("Avatar not found for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Avatar not found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Error fetching avatar for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch avatar", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping(value = "/users/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @PreAuthorize("#userId == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("avatar") MultipartFile file) {

        try {
            logger.debug("Uploading avatar for user ID: {}", userId);

            if (file.isEmpty()) {
                logger.warn("Empty file uploaded for user: {}", userId);
                throw new IllegalArgumentException("File cannot be empty");
            }

            if (!userService.existsById(userId)) {
                logger.warn("User not found: {}", userId);
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            // Validate file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds maximum limit of 2MB");
            }

            userService.updateAvatar(userId, file);
            logger.debug("Avatar updated successfully for user: {}", userId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SuccessResponse("Avatar updated successfully"));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid avatar upload request for user: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid request", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found during avatar upload: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (IOException e) {
            logger.error("Failed to upload avatar for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to upload avatar", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            logger.error("Unexpected error uploading avatar for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authService.authenticate(request);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            logger.error("Login failed for user: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setRoles(new HashSet<>(request.getRoles()));

            User createdUser = authService.createUser(user, request.getPassword());
            String token = jwtService.generateToken(createdUser);

            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(token)
                    .id(createdUser.getId())
                    .email(createdUser.getEmail())
                    .roles(createdUser.getRoles())
                    .build();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // Helper classes for standardized responses
    private static class SuccessResponse {
        private final boolean success = true;
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ErrorResponse {
        private final boolean success = false;
        private final String message;
        private final String details;
        private final int status;

        public ErrorResponse(String message, String details, int status) {
            this.message = message;
            this.details = details;
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }

        public int getStatus() {
            return status;
        }
    }
}