package com.example.carrotmarketbackend.RefreshToken;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String email, String refreshToken, LocalDateTime refreshTokenExpiryDate) {
        try {
            // 기존 토큰 조회
            Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByEmail(email);

            // 기존 토큰이 있으면 삭제
            existingTokenOptional.ifPresent(refreshTokenRepository::delete);

            // 새 토큰 생성
            RefreshToken newToken = RefreshToken.builder()
                    .email(email)
                    .token(refreshToken)
                    .expiryDate(refreshTokenExpiryDate)
                    .build();

            // 새 토큰 저장
            refreshTokenRepository.save(newToken);

            // 로그 추가
            log.info("Successfully saved refresh token for email: {}", email);

        } catch (Exception e) {
            // 예외 처리
            log.error("Error while saving refresh token for email: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Failed to save refresh token", e);
        }
    }


    public void validateRefreshToken(String email, String refreshToken) {
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
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired.");
        }

        // 5. 추가적인 토큰 검증이 필요할 경우 추가
        // 예를 들어, 토큰이 특정 형식을 따르는지 또는 서명이 올바른지 확인할 수 있습니다.
    }

}
