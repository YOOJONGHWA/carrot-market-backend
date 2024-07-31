package com.example.carrotmarketbackend.Enum;

import com.example.carrotmarketbackend.common.enums.BaseEnum;
import lombok.Getter;
@Getter
public enum UserStatusEnum implements BaseEnum {

    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),
    USER_NOT_FOUND(402, "USER_NOT_FOUND"),;

    public final int statusCode;
    public final String message;

    UserStatusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.message = code;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

