package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.UserStatusEnum;
import com.example.carrotmarketbackend.RefreshToken.RefreshToken;
import com.example.carrotmarketbackend.RefreshToken.RefreshTokenRequest;
import com.example.carrotmarketbackend.RefreshToken.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserStatusEnum> signup(@Valid @RequestBody UserDto dto) {
        return userService.save(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            TokenResponse tokenResponse = userService.login(request.getEmail(), request.getPassword(), response);
            log.info("getAccessToken returned: {}", tokenResponse.getAccessToken());
            return ResponseEntity.ok(tokenResponse.getAccessToken()); // 엑세스 토큰만 응답으로 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        try {
            String refreshToken = request.getRefreshToken();
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token is missing or empty");
            }
            String newAccessToken = userService.refreshAccessToken(refreshToken);

            // 새로운 액세스 토큰을 쿠키에 저장
            Cookie accessTokenCookie = JwtUtil.createJwtCookie(newAccessToken);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok(newAccessToken);
        } catch (RuntimeException e) {
            log.info("refresh returned: {}",request.getRefreshToken());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰 오류: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

}
