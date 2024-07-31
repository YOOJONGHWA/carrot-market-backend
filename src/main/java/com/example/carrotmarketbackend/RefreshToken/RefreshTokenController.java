package com.example.carrotmarketbackend.RefreshToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        try {
            String refreshToken = request.getRefreshToken();
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token is missing or empty");
            }

            // 새로운 액세스 토큰 생성
            String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);

            // 새로운 액세스 토큰을 쿠키에 저장
            Cookie accessTokenCookie = JwtUtil.createJwtCookie(newAccessToken);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok(String.valueOf(newAccessToken));
        } catch (RuntimeException e) {
            log.error("Refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰 오류: " + e.getMessage());
        }
    }

}
