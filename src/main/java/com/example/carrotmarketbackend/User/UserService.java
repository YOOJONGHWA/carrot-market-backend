package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.UserStatusEnum;
import com.example.carrotmarketbackend.RefreshToken.RefreshTokenService;
import com.example.carrotmarketbackend.RefreshToken.TokenResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public TokenResponse login(String email, String password, HttpServletResponse response) {
        try {
            // 사용자 인증
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 토큰 생성
            String accessToken = JwtUtil.createToken(auth);
            String refreshToken = JwtUtil.createRefreshToken(email);
            log.info("Refresh token: {}", refreshToken);
            log.info("Access token: {}", accessToken);
            // 리프레시 토큰 데이터베이스에 저장
            refreshTokenService.saveRefreshToken(email, refreshToken, JwtUtil.getRefreshTokenExpiryDate());

            // 액세스 토큰을 쿠키에 저장
            Cookie accessTokenCookie = JwtUtil.createJwtCookie(accessToken);
            response.addCookie(accessTokenCookie);

            // 리프레시 토큰을 응답으로 반환
            return new TokenResponse(accessToken); // 액세스 토큰만 반환

        } catch (AuthenticationException e) {
            throw new RuntimeException("인증 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("서버 에러: " + e.getMessage());
        }
    }

    public String refreshAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사 및 사용자 정보 확인
        Claims claims = JwtUtil.extractToken(refreshToken);
        String email = claims.get("email", String.class);

        // 2. 저장된 리프레시 토큰의 유효성을 검사
        refreshTokenService.validateRefreshToken(email, refreshToken);

        // 3. 사용자 권한 정보를 포함하여 인증 객체 생성
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        // 현재 사용자의 권한을 포함하여 새로운 인증 객체 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                null,
                auth.getAuthorities()
        );

        // 4. 새로운 액세스 토큰 생성
        return JwtUtil.createToken(authToken);
    }

    public ResponseEntity<UserStatusEnum> save(UserDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profileImage(dto.getProfileImage())
                .email(dto.getEmail())
                .bio(dto.getBio())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserStatusEnum.OK);
    }
}
