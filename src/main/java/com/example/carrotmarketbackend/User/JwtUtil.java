package com.example.carrotmarketbackend.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    static final SecretKey key =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
            ));

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
                .expiration(new Date(System.currentTimeMillis() + 3600000)) //유효기간 10초
                .signWith(key)
                .compact();
        System.out.println("Generated JWT: " + jwt);  // JWT 출력
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token).getPayload();
        return claims;
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

}
