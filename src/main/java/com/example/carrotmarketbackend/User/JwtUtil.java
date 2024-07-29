package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.JwtEnum;
import com.example.carrotmarketbackend.Exception.JwtExceptionHandler;
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
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class JwtUtil {

    private static final long EXPIRATION_TIME = 3600000; // 1시간
    private static final long EXPIRATION_COOKIE_TIME = 3600; // 1시간

    private static String secretKey;

    @Value("${jwt.secret-key}")
    private void setSecretKey(String value) {
        secretKey = value;
    }

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
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
    public static Claims extractToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("JWT Issued At: {}", claims.getIssuedAt());
            log.info("JWT Expires At: {}", claims.getExpiration());
            log.info("Current Server Time: {}", new Date(System.currentTimeMillis()));

            return claims;

        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtExceptionHandler(JwtEnum.JWT_PROCESSING_ERROR);
        }
    }

    // Type Conversion Helper
    public static Long getLongClaim(Claims claims, String claimKey) {
        Object claimValue = claims.get(claimKey);
        if (claimValue instanceof Number) {
            return ((Number) claimValue).longValue();
        }
        return null;
    }

    // JWT 리프레시 메서드
    public static String refreshToken(String token)  {
        try {
            Claims claims = extractToken(token);
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(getSecretKey())
                    .compact();
        }
        catch (JwtException | IllegalArgumentException ex) {
            throw new JwtExceptionHandler(JwtEnum.JWT_PROCESSING_ERROR);
        }
    }

    public static Cookie createJwtCookie(String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge((int) EXPIRATION_COOKIE_TIME); // 쿠키의 유효 시간 (1시간)
        cookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 접근 불가능
        cookie.setPath("/"); // 도메인 전체에서 쿠키 접근 가능
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        return cookie;
    }
}
