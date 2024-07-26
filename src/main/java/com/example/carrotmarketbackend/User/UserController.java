package com.example.carrotmarketbackend.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/signup")
    public ResponseEntity<StatusEnum> signup(@Valid @RequestBody UserDto dto) {

        return userService.save(dto);

    }

    @PostMapping("/login")
    public String loginJwt(@RequestBody UserDto dto, HttpServletResponse response) {
        try {

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            var jwt = JwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());
            System.out.println(jwt);

            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setMaxAge(3600); // 쿠키의 유효 시간 (1시간)
            cookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 접근 불가능
            cookie.setPath("/"); // 도메인 전체에서 쿠키 접근 가능
            cookie.setSecure(true); // HTTPS 환경에서만 전송
            response.addCookie(cookie);

            return jwt;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/me")
    public CustomUser getCurrentUser() {
        // 현재 인증된 사용자 정보를 SecurityContext에서 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUser) {
            return (CustomUser) authentication.getPrincipal();
        }
        throw new RuntimeException("사용자 정보가 없습니다.");
    }
}
