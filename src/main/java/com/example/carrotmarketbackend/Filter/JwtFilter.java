package com.example.carrotmarketbackend.Filter;

import com.example.carrotmarketbackend.User.CustomUser;
import com.example.carrotmarketbackend.User.JwtUtil;
import io.jsonwebtoken.Claims;
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
            Claims claims = JwtUtil.extractToken(jwtToken);
            String username = claims.get("username", String.class);
            String email = claims.get("email", String.class);
            Long id = JwtUtil.getLongClaim(claims, "id"); // Use the helper method to get Long
            String authoritiesStr = claims.get("authorities", String.class);

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
                System.out.println(authenticationToken + " ?>>>>");
            }
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT 토큰 유효기간 만료: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("JWT 토큰 처리 중 오류 발생: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
