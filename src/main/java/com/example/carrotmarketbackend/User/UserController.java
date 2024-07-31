package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.LoginResponseStatus;
import com.example.carrotmarketbackend.RefreshToken.JwtUtil;
import com.example.carrotmarketbackend.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<ApiResponse<CreateUser.Response>> signup(@Valid @RequestBody CreateUser.Request request) {
        log.info("request: {}", request);
        return userService.save(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginJwt.TokenResponse>> login(@Valid  @RequestBody LoginJwt.LoginRequest request,HttpServletRequest cookie,  HttpServletResponse response) {
            return userService.login(request, response,cookie);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 삭제
        Cookie[] cookies = request.getCookies();
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
        return "로그아웃 성성";
    }

    @GetMapping("/me")
    public ResponseEntity<CustomUser> getCurrentUser(Authentication authentication) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

}
