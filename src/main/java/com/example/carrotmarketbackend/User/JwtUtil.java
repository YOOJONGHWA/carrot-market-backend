package com.example.carrotmarketbackend.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class JwtUtil {

    private static final long EXPIRATION_TIME = 3600000; // 1시간 (밀리초 단위)
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 2592000000L; // 30일 (밀리초 단위)
    private static final long EXPIRATION_COOKIE_TIME = 3600;

    private static String secretKey;


    @Value("${jwt.secret-key}")
    private void setSecretKey(String value) {
        secretKey = value;
    }

    private static SecretKey getSecretKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (IllegalArgumentException e) {
            log.error("Invalid secret key: {}", e.getMessage());
            throw new RuntimeException("Invalid secret key", e);
        }
    }

    // JWT 생성 메서드
    public static String createToken(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        var authorities = auth.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(","));
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + EXPIRATION_TIME);

        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("id", user.getId())
                .claim("authorities", authorities)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();

        log.info("Generated JWT: {}", jwt);
        log.info("Issued at: {}", issuedAt);
        log.info("Expires at: {}", expiration);
        return jwt;
    }

    // JWT 파싱 메서드
    public static Claims extractToken(String jwtToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey().getEncoded())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            log.info("JWT Issued At: {}", claims.getIssuedAt());
            log.info("JWT Expires At: {}", claims.getExpiration());
            log.info("Current Server Time: {}", new Date(System.currentTimeMillis()));

            return claims;
        } catch (ExpiredJwtException ex) {
            // 토큰이 만료된 경우
            log.warn("JWT expired: {}", ex.getMessage());
            return ex.getClaims();
        } catch (JwtException | IllegalArgumentException ex) {
            // JWT 처리 오류 또는 잘못된 토큰
            log.error("JWT processing error: {}", ex.getMessage());
            throw new JwtException("Invalid JWT token");
        }
    }


    // JWT 리프레시 메서드
    public static String createRefreshToken(String email) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        String refreshToken = Jwts.builder()
                .claim("email", email)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();

        log.info("Generated Refresh Token: {}", refreshToken);
        log.info("Issued at: {}", issuedAt);
        log.info("Expires at: {}", expiration);
        return refreshToken;
    }

    // JWT 만료일 계산 메서드
    public static LocalDateTime getRefreshTokenExpiryDate() {
        // 현재 시간을 LocalDateTime으로 변환
        LocalDateTime now = LocalDateTime.now();

        // 만료 기간을 Duration으로 정의
        Duration expirationDuration = Duration.ofMillis(REFRESH_TOKEN_EXPIRATION_TIME);

        // 만료일 계산
        return now.plus(expirationDuration);
    }

    public static Cookie createJwtCookie(String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge((int) EXPIRATION_COOKIE_TIME); // 쿠키의 유효 시간 (1시간)
        cookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 접근 불가능
        cookie.setPath("/"); // 도메인 전체에서 쿠키 접근 가능
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        return cookie;
    }

    public static Long getLongClaim(Claims claims, String claimKey) {
        Object claimValue = claims.get(claimKey);
        if (claimValue instanceof Number) {
            return ((Number) claimValue).longValue();
        }
        return null;
    }

}
