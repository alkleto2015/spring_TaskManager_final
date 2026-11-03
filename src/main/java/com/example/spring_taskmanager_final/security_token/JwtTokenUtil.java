package com.example.spring_taskmanager_final.security_token;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final SecretKey key;
    private static final long EXPIRATION = 1000L * 60 * 60 * 10; // 10 годин в мілісекундах

    public JwtTokenUtil(@Value("${jwt.secret:01234567890123456789012345678901}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)   // Записываем роль в токен
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Метод для витягування ролі з токена
    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    // витягує username з токена. Якщо токен просрочен або підроблений — вертає null
    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("ПОМИЛКА ВАЛІДАЦІЇ ТОКЕНА: " + e.getMessage());
            return null;
        }
    }
}
