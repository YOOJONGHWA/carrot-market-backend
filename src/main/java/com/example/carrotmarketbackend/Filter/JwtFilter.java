package com.example.carrotmarketbackend.Filter;

import com.example.carrotmarketbackend.User.CustomUser;
import com.example.carrotmarketbackend.User.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
            String username = JwtUtil.extractToken(jwtToken).get("username", String.class);
            String email = JwtUtil.extractToken(jwtToken).get("email", String.class);
            Long id = JwtUtil.getLongClaim(JwtUtil.extractToken(jwtToken), "id");
            String authoritiesStr = JwtUtil.extractToken(jwtToken).get("authorities", String.class);

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
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰 유효기간이 만료되었습니다.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰 처리 중 오류가 발생하였습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
