package com.attendance.scheduler.infra.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰 발급/검증 담당.
 * access 토큰에 username 과 권한(roles) 을 담는다.
 */
@Slf4j
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
            @Value("${jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public String createAccessToken(String username, List<String> roles) {
        return createToken(username, roles, accessTokenValiditySeconds);
    }

    public String createRefreshToken(String username) {
        return createToken(username, List.of(), refreshTokenValiditySeconds);
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    private String createToken(String username, List<String> roles, long validitySeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validitySeconds * 1000);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            log.debug("유효하지 않은 JWT: {}", e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object roles = parse(token).getPayload().get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
