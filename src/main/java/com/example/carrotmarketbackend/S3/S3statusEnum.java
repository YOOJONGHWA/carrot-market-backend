package com.example.carrotmarketbackend.S3;

import lombok.Getter;

@Getter
public enum S3statusEnum {

    UPLOAD_OK(200, "OK"),
    UPLOAD_FAIILE(400, "BAD_REQUEST"),
    DOWNLOAD_OK(200, "OK"),
    DOWNLOAD_FAIILE(400, "BAD_REQUEST"),
    INVAILD_URL(400, "BAD_REQUEST"),
    INTERNAL_SERER_ERROR(500, "INTERNAL_SERVER_ERROR");

    int statusCode;
    String code;

    S3statusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}

