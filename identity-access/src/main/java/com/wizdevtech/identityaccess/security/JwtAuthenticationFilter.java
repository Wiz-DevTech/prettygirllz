// src/main/java/com/wizdevtech/identityaccess/security/JwtAuthenticationFilter.java
package com.wizdevtech.identityaccess.security;

import com.wizdevtech.identityaccess.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") ||
                path.equals("/health") ||
                path.equals("/api/avatars/preview");
    }

    private final JwtService jwtService;



    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT
            final String jwt = authHeader.substring(7);

            // Validate JWT
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract all claims
                Map<String, Object> claims = jwtService.extractAllClaims(jwt);

                if (!jwtService.isTokenExpired(jwt)) {
                    // Get user ID
                    String userId = jwtService.extractUsername(jwt);

                    // Get roles
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("roles");

                    // Create authorities
                    var authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    // Create authentication token
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception ignored) {
            // JWT is invalid, continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}