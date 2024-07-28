package com.example.carrotmarketbackend.Enum;

import lombok.Getter;

@Getter
public enum S3statusEnum {

    UPLOAD_OK(200, "OK"),
    UPLOAD_FAIL(400, "BAD_REQUEST"),
    INVALID_URL(400, "BAD_REQUEST"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    public final int statusCode;
    public final String code;

    S3statusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}

