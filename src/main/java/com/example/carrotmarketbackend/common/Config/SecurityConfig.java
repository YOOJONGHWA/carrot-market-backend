package com.example.carrotmarketbackend.common.Config;

import com.example.carrotmarketbackend.common.Filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorize) ->
                authorize.requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/signup"
                                        ,"/api/auth/login"
                                        ,"api/auth/refresh")
                                        .permitAll()
                        .anyRequest().authenticated()
        );

        http.logout(logout -> logout.logoutUrl("api/auth/logout").logoutSuccessUrl("/"));

        http.addFilterBefore(new JwtFilter(), ExceptionTranslationFilter.class);

        return http.build();
    }
}
