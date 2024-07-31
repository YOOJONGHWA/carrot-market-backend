package com.example.carrotmarketbackend.common.Filter;

import com.example.carrotmarketbackend.RefreshToken.JwtUtil;
import com.example.carrotmarketbackend.User.CustomUser;
import com.example.carrotmarketbackend.common.Exception.Custom.JwtExceptionHandler;
import com.example.carrotmarketbackend.common.Exception.JwtErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/api/auth/signup") || path.equals("/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = JwtUtil.extractToken(jwtToken);
            if (claims == null) {
                log.error("Claims could not be extracted from JWT.");
                throw new JwtExceptionHandler(JwtErrorCode.INVALID_SECRET_KEY);
            }

            String username = claims.get("username", String.class);
            String email = claims.get("email", String.class);
            Long id = JwtUtil.getLongClaim(claims, "id");
            String authoritiesStr = claims.get("authorities", String.class);

            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();

            log.info("JWT Issued At: {}", issuedAt);
            log.info("JWT Expires At: {}", expiration);
            log.info("Current Server Time: {}", new Date(System.currentTimeMillis()));

            if (username != null && authoritiesStr != null) {
                var authorities = Arrays.stream(authoritiesStr.split(","))
                        .map(SimpleGrantedAuthority::new).toList();

                CustomUser customUser = new CustomUser(username, "none", authorities);
                customUser.setEmail(email);
                customUser.setId(id);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        customUser, null, authorities);

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException ex) {
            log.error("JWT Token is expired: {}", ex.getMessage());
            throw new JwtExceptionHandler(JwtErrorCode.JWT_EXPIRED);
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("JWT Token processing error: {}", ex.getMessage());
            throw new JwtExceptionHandler(JwtErrorCode.JWT_PROCESSING_ERROR);
        }
        filterChain.doFilter(request, response);
    }
}
