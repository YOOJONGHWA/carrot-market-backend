package com.example.carrotmarketbackend.Enum;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtEnum {
    JWT_EXPIRED("JWT 토큰 유효기간 만료", 401),
    JWT_PROCESSING_ERROR("JWT 토큰 처리 중 오류 발생", 500),
    REFRESH_JWT_PROCESSING_ERROR("JWT 리프레시 토큰 처리 중 오류 발생", 500),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", 403);

    private final String message;
    private final int status;

    JwtEnum(String message, int status) {
        this.message = message;
        this.status = status;
    }

}