package com.example.carrotmarketbackend.RefreshToken;

import com.example.carrotmarketbackend.User.CustomUser;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String email, String refreshToken, LocalDateTime refreshTokenExpiryDate) {
        try {
            // 이메일로 기존 리프레시 토큰 조회
            Optional<RefreshToken> optionalToken = refreshTokenRepository.findByEmail(email);
            // 기존 리프레시 토큰이 존재하면 삭제
            optionalToken.ifPresent(refreshTokenRepository::delete);

            // 새 RefreshToken 엔티티 생성
            RefreshToken newToken = RefreshToken.builder()
                    .email(email)
                    .token(refreshToken)
                    .expireDate(refreshTokenExpiryDate)
                    .build();

            // 데이터베이스에 새 리프레시 토큰 저장
            refreshTokenRepository.save(newToken);

            log.info("Successfully saved refresh token for email: {}", email);

        } catch (Exception e) {
            log.error("Error while saving refresh token for email: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Failed to save refresh token", e);
        }
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims;
        try {
            claims = JwtUtil.extractToken(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }

        String email = claims.get("email", String.class);
        Date expiration = claims.getExpiration();
        LocalDateTime expirationLocalDateTime = toLocalDateTime(expiration);

        validateRefreshToken(email, refreshToken, expirationLocalDateTime);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
            throw new RuntimeException("User not authenticated or not of expected type");
        }

        return JwtUtil.createToken(auth);
    }


    public void validateRefreshToken(String email, String refreshToken, LocalDateTime expirationLocalDateTime) {
        // 1. 데이터베이스에서 리프레시 토큰 조회
        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByEmail(email);

        // 2. 토큰 존재 여부 확인
        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Refresh token not found for the given email.");
        }

        RefreshToken storedToken = optionalToken.get();

        // 3. 저장된 토큰과 요청된 토큰 비교
        if (!storedToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token provided.");
        }

        // 4. 토큰 만료 시간 확인
        if (storedToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired.");
        }

        // 5. 추가적인 토큰 검증이 필요할 경우 추가
        // 예를 들어, 토큰이 특정 형식을 따르는지 또는 서명이 올바른지 확인할 수 있습니다.
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
