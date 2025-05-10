package com.wizdevtech.identityaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for authentication operations
 * Contains user identity information and authentication status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private Long id;
    private String email;
    private Set<String> roles;
    private String token;
    private boolean permitted;
    private String resourceId;
    private String action;
    private String reason;
}