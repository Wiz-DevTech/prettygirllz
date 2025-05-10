package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token generation, validation and parsing
 */
@Service
public class JwtService {

    private final String jwtSecret;
    private final long jwtExpiration;

    @Setter
    private Clock clock = Clock.DEFAULT;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expiration}") long jwtExpiration) {
        // Remove any characters that could cause Base64 decoding issues
        this.jwtSecret = jwtSecret.replace('_', '-').replace('/', '-');
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles());

        return createToken(claims, user.getId().toString());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(clock.millis());
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        // Use the secret directly as bytes without Base64 decoding
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Return empty claims in case of parsing error
            return Jwts.claims();
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration == null || expiration.before(new Date(clock.millis()));
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validates JWT token against user details
     * @param token JWT token to validate
     * @param userDetails User details to validate against (can be null for structural validation only)
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // Basic token structure and signature validation
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);

            // Check if token is expired
            if (isTokenExpired(token)) {
                return false;
            }

            // If userDetails is provided, validate username/subject
            if (userDetails != null) {
                final String username = extractUsername(token);
                return username != null && username.equals(userDetails.getUsername());
            }

            // If only validating structure and expiration (not against a specific user)
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Alias method for validateToken with null userDetails
     * Just checks token structure and expiration
     */
    public boolean isTokenValid(String token) {
        return validateToken(token, null);
    }

    // Clock interface for testing
    public interface Clock {
        long millis();

        // Default implementation using method reference
        Clock DEFAULT = System::currentTimeMillis;
    }
}