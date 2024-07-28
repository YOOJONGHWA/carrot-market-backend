package com.example.carrotmarketbackend.Enum;

import lombok.Getter;

@Getter
public enum RoleEnum {

    ADMIN("관리자"),
    USER("유저");

    public final String role;

    RoleEnum(String role) {
        this.role = role;
    }

}
