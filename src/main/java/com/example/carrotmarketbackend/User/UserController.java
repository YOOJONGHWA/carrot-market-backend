package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.JwtEnum;
import com.example.carrotmarketbackend.Enum.UserStatusEnum;
import com.example.carrotmarketbackend.Exception.JwtExceptionHandler;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserStatusEnum> signup(@Valid @RequestBody UserDto dto) {

        return userService.save(dto);

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginJwt(@RequestBody UserDto dto, HttpServletResponse response) {
        try {

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            var jwt = JwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());

            Cookie cookie = JwtUtil.createJwtCookie(jwt);
            response.addCookie(cookie);

            return ResponseEntity.ok().body(jwt);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("서버오류");
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


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(String refreshToken, HttpServletResponse response) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new JwtExceptionHandler(JwtEnum.INVALID_REFRESH_TOKEN);
            }

            Claims claims = JwtUtil.extractToken(refreshToken);
            if (claims == null) {
                throw new JwtExceptionHandler(JwtEnum.INVALID_REFRESH_TOKEN);
            }

            String email = claims.get("email", String.class);
            if (email == null) {
                throw new JwtExceptionHandler(JwtEnum.INVALID_REFRESH_TOKEN);
            }

            final String newJwt = JwtUtil.refreshToken(refreshToken);
            Cookie jwtCookie = JwtUtil.createJwtCookie(newJwt);
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(newJwt);
        } catch (JwtExceptionHandler e) {
            return ResponseEntity.status(e.getError().getStatus()).body(e.getError().getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(JwtEnum.JWT_PROCESSING_ERROR.getStatus()).body(JwtEnum.JWT_PROCESSING_ERROR.getMessage());
        }
    }


}
