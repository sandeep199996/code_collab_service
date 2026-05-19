package com.mentorplatform.Backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // The server's ultra-secret signature. If a hacker doesn't know this, they can't forge a token!
    // (Note: In a real enterprise app, we hide this inside application.properties)
    private final String SECRET_STRING = "ThisIsASecretKeyForMentorPlatform123456789";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Generate the VIP Pass
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email) // Who does this pass belong to?
                .issuedAt(new Date(System.currentTimeMillis())) // When was it created?
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expires in 10 hours
                .signWith(key) // Sign it with our un-forgeable secret key
                .compact();
    }
    // Extract the email (subject) from the token
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Check if the token is valid and signed by us
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token is expired, tampered with, or invalid
            return false;
        }
    }
}