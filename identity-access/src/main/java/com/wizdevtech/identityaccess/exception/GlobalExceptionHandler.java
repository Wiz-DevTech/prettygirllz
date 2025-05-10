package com.wizdevtech.identityaccess.exception;

import com.wizdevtech.identityaccess.service.AvatarService.AvatarServiceException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for consistent API error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. For duplicate email (Data Integrity)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDuplicateEmail(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Resource Conflict");
        problemDetail.setDetail("Email already exists");
        problemDetail.setProperty("path", "/api/auth/register");
        return problemDetail;
    }

    // 2. For Bean Validation errors
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");

        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> "%s: %s".formatted(v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.toList());

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    // 3. For custom authentication exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication failed");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    // 4. For Spring Security exceptions
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ProblemDetail handleSecurityExceptions(Exception ex) {
        logger.warn("Security exception: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication failed");
        problemDetail.setDetail("Invalid credentials");
        return problemDetail;
    }

    // 5. For @Valid method argument validation
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        logger.warn("Method argument not valid: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        problemDetail.setProperty("errors", errors);
        return new ResponseEntity<>(problemDetail, headers, status);
    }

    // 6. For JSON parsing errors
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        logger.warn("JSON parse error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request format");
        problemDetail.setDetail("Could not parse request body. Check JSON syntax.");
        return new ResponseEntity<>(problemDetail, headers, status);
    }

    // 7. For illegal arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    // 8. For resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    // 9. For avatar service exceptions
    @ExceptionHandler(AvatarServiceException.class)
    public ProblemDetail handleAvatarServiceException(AvatarServiceException ex) {
        logger.error("Avatar service error: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatus());
        problemDetail.setTitle("Avatar processing error");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    // 10. For file upload exceptions
    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    public ProblemDetail handleFileUploadExceptions(Exception ex) {
        logger.warn("File upload error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("File upload failed");
        problemDetail.setDetail(ex instanceof MaxUploadSizeExceededException
                ? "File size exceeds maximum limit"
                : "Invalid file upload request");
        return problemDetail;
    }

    // 11. Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal server error");
        problemDetail.setDetail("An unexpected error occurred");
        return problemDetail;
    }
}