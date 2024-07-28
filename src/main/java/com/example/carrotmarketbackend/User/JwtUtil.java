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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JwtUtil {

    private static final long EXPIRATION_TIME = 3600000; // 1시간
    private static final long EXPIRATION_COOKIE_TIME = 3600000; // 1시간

    private static String secretKey;

    @Value("${jwt.secret-key}")
    private void setSecretKey(String value) {
        secretKey = value;
    }

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // JWT 만들어주는 함수
    public static String createToken(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        var authorities = auth.getAuthorities().stream().map(authority -> authority.toString()).collect(Collectors.joining(","));
        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("id", user.getId())
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //유효기간 10초
                .signWith(getSecretKey())
                .compact();
        System.out.println("Generated JWT: " + jwt);  // JWT 출력
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
        }
        catch (ExpiredJwtException ex){
            throw new JwtExceptionHandler(JwtEnum.JWT_EXPIRED,ex);
        }
        catch (JwtException | IllegalArgumentException ex){
            throw new JwtExceptionHandler(JwtEnum.JWT_PROCESSING_ERROR, ex);
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

    /*
    *   to do jwt 를 리프레시 하는 기능도 추가하자
    * */
    // JWT 리프레시 메서드
    public static String refreshToken(String token) {
        try {
            Claims claims = extractToken(token);
            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(getSecretKey())
                    .compact();
        }
        catch (ExpiredJwtException ex){
            throw new JwtExceptionHandler(JwtEnum.JWT_EXPIRED,ex);
        }
        catch (JwtException | IllegalArgumentException ex){
            throw new JwtExceptionHandler(JwtEnum.JWT_PROCESSING_ERROR, ex);
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
