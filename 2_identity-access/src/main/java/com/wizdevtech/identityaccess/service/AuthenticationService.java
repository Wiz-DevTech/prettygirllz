package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.dto.AuthenticationRequest;
import com.wizdevtech.identityaccess.dto.AuthenticationResponse;
import com.wizdevtech.identityaccess.dto.RegistrationRequest;
import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for handling authentication, user management, and token operations
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    /**
     * Find users with optional search filter
     * @param search Optional search term for filtering by email
     * @param pageable Pagination information
     * @return Paginated list of users
     */
    public Page<User> findUsers(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return userRepository.findByEmailContainingIgnoreCase(search, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    /**
     * Update user information
     * @param id User ID
     * @param email New email
     * @param roles New roles
     * @return Updated user
     */
    @Transactional
    public User updateUser(Long id, String email, Set<String> roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Validate email format
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check if email is already in use by another user
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setEmail(email);
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        }

        logger.info("Updating user: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Delete a user
     * @param id User ID to delete
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent deletion of admin users
        if (user.getId().equals(User.ADMIN_ID) || user.getRoles().contains("ROLE_ADMIN")) {
            throw new RuntimeException("Cannot delete admin users");
        }

        logger.info("Deleting user: {}", user.getId());
        userRepository.delete(user);
    }

    /**
     * Authenticate a user with credentials
     * @param request Authentication request containing email and password
     * @return Authentication response with JWT token and user details
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            logger.info("Authenticating user: {}", request.getEmail());

            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            // Generate JWT token
            String token = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(token)
                    .id(user.getId())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .permitted(true)
                    .build();
        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", request.getEmail(), e.getMessage());
            return AuthenticationResponse.builder()
                    .token(null)
                    .id(null)
                    .email(null)
                    .roles(null)
                    .permitted(false)
                    .build();
        }
    }

    /**
     * Register a new user
     * @param request Registration request containing user details
     * @return Authentication response with JWT token
     */
    @Transactional
    public AuthenticationResponse register(RegistrationRequest request) {
        logger.info("Processing registration for email: {}", request.getEmail());

        // Validate input data
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        if (!request.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create user entity from request
        User user = new User();
        user.setEmail(request.getEmail());
        user.setRoles(request.getRoles() != null && !request.getRoles().isEmpty()
                ? new HashSet<>(request.getRoles())
                : Set.of("ROLE_USER"));

        // Create user in database
        User createdUser = createUser(user, request.getPassword());

        // Generate token for auto login
        String token = jwtService.generateToken(createdUser);

        return AuthenticationResponse.builder()
                .token(token)
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .roles(createdUser.getRoles())
                .permitted(true)
                .build();
    }

    /**
     * Create a new user with encoded password
     * @param user User entity
     * @param rawPassword Raw password to encode
     * @return Created user
     */
    @Transactional
    public User createUser(User user, String rawPassword) {
        // Check if user exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        // Encrypt sensitive data if present
        if (user.getSensitiveData() != null) {
            user.setSensitiveData(encryptionService.encrypt(user.getSensitiveData()));
        }

        logger.info("Creating new user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Validate JWT token
     * @param token JWT token to validate
     * @return Authentication response with user details if valid
     */
    public AuthenticationResponse validateToken(String token) {
        try {
            logger.debug("Validating token");

            // First check if token is valid structurally and not expired
            if (!jwtService.isTokenValid(token)) {
                logger.error("Token validation failed: Invalid token format or expired");
                return createFailedAuthResponse();
            }

            // Extract email from token
            String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
            if (email == null) {
                logger.error("Token validation failed: Email claim missing");
                return createFailedAuthResponse();
            }

            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if the token subject matches user ID
            String subject = jwtService.extractUsername(token);
            if (!subject.equals(user.getId().toString())) {
                logger.error("Token validation failed: Subject does not match user ID");
                return createFailedAuthResponse();
            }

            return AuthenticationResponse.builder()
                    .token(token)
                    .id(user.getId())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .permitted(true)
                    .build();
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return createFailedAuthResponse();
        }
    }

    /**
     * Helper method to create a failed authentication response
     */
    private AuthenticationResponse createFailedAuthResponse() {
        return AuthenticationResponse.builder()
                .token(null)
                .id(null)
                .email(null)
                .roles(null)
                .permitted(false)
                .build();
    }
}