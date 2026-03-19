package com.example.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

//create and read JWT tokens
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generate key from secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes()); //secret string converted to cryptographic key
    }

    // Generate token from user details
  public String generateToken(String email, String role, Long userId) {
    return Jwts.builder()
        .setSubject(email)           // who this token is for
        .claim("role", role)         // their role (STUDENT/TRAINER/ADMIN)
        .claim("userId", userId)     // their database ID
        .setIssuedAt(new Date())     // when was it created
        .setExpiration(new Date(System.currentTimeMillis() + expiration)) // when it dies
        .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign it
        .compact();                  // serialize to a string
}
    // Extract email from token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Extract userId from token
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Get all claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}