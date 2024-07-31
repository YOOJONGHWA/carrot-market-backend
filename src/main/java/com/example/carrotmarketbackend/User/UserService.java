package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.RefreshToken.JwtUtil;
import com.example.carrotmarketbackend.RefreshToken.RefreshTokenService;
import com.example.carrotmarketbackend.common.Exception.Custom.UserExceptionHandler;
import com.example.carrotmarketbackend.common.Exception.UserErrorCode;
import com.example.carrotmarketbackend.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<ApiResponse<LoginJwt.TokenResponse>> login(LoginJwt.LoginRequest request, HttpServletResponse response, HttpServletRequest RequestCokie) {

        // 쿠키 삭제
        Cookie[] cookies = RequestCokie.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    // 쿠키 삭제
                    Cookie emptyCookie = JwtUtil.createEmptyJwtCookie();
                    response.addCookie(emptyCookie);
                    break;
                }
            }
        }

        validateLoginJwtLoginRequest(request);

        String email = request.getEmail();
        String password = request.getPassword();

        // 사용자 인증
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 토큰 생성
        String accessToken = JwtUtil.createToken(auth);
        String refreshToken = JwtUtil.createRefreshToken(auth);
        log.info("Refresh token: {}", refreshToken);
        log.info("Access token: {}", accessToken);

        // 리프레시 토큰 데이터베이스에 저장
        refreshTokenService.saveRefreshToken(email, refreshToken, JwtUtil.getRefreshTokenExpiryDate());

        // 엑세스 토큰을 쿠키에 저장
        Cookie accessTokenCookie = JwtUtil.createJwtCookie(accessToken);
        response.addCookie(accessTokenCookie);

        // 응답 생성 및 반환
        ApiResponse<LoginJwt.TokenResponse> tokenResponse = LoginJwt.TokenResponse.fromJwt(accessToken,refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    private void validateLoginJwtLoginRequest(LoginJwt.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserExceptionHandler(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserExceptionHandler(UserErrorCode.USER_NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<CreateUser.Response>> save(CreateUser.Request request) {

        validateCreateUserRequest(request);

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImage(request.getProfileImage())
                .email(request.getEmail())
                .bio(request.getBio())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // 응답 생성 및 반환
        ApiResponse<CreateUser.Response> response = CreateUser.Response.fromEntity(user);
        return ResponseEntity.ok(response);
    }

    private void validateCreateUserRequest(CreateUser.Request request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserExceptionHandler(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자 이름 중복 검사
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserExceptionHandler(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }
    }

    @Transactional
    public void logout(HttpServletResponse response) {

        Cookie emptyCookie = JwtUtil.createEmptyJwtCookie();
        response.addCookie(emptyCookie);

    }
}
