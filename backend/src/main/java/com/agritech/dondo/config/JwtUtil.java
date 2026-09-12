package com.agritech.dondo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:agritechDondoSecretKeySuperSeguraDistritoDeSofala2026MocambiqueCorredorBeiraChinamacondo}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String telemovel, String perfil, String nome, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("perfil", perfil);
        claims.put("nome", nome);
        claims.put("id", id);

        return Jwts.builder()
                .claims(claims)
                .subject(telemovel)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractTelemovel(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractPerfil(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("perfil", String.class);
    }

    public Long extractId(String token) {
        Claims claims = extractAllClaims(token);
        Object id = claims.get("id");
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, String telemovel) {
        final String tokenTelemovel = extractTelemovel(token);
        return (tokenTelemovel.equals(telemovel) && !isTokenExpired(token));
    }
}
