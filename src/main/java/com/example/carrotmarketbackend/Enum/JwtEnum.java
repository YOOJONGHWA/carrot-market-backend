package com.example.carrotmarketbackend.Enum;

public enum JwtEnum {

    JWT_EXPIRED("JWT 토큰 유효기간 만료"),
    JWT_PROCESSING_ERROR("JWT 토큰 처리 중 오류 발생");

    public final String meassage;

    JwtEnum(String meassage) {
        this.meassage = meassage;
    }
}
