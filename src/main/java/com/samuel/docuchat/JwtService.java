package com.samuel.docuchat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key = Keys.hmacShaKeyFor(
        "docuchat-dev-secret-key-change-this-in-production-32bytes".getBytes()
    );

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    public String generateToken(UUID userId, String email, UUID orgId) {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("orgId", orgId.toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(key)
            .compact();
    }

    public io.jsonwebtoken.Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID extractOrgId(String token) {
        String raw = token.startsWith("Bearer ") ? token.substring(7) : token;
        io.jsonwebtoken.Claims claims = parseToken(raw);
        return UUID.fromString(claims.get("orgId", String.class));
    }
}
