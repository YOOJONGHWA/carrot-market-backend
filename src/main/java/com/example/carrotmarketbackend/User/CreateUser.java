package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.UserStatusEnum;
import com.example.carrotmarketbackend.common.dto.ApiResponse;
import com.example.carrotmarketbackend.common.validation.ValidationMessages;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class CreateUser {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request {

        @NotBlank(message = ValidationMessages.NAME_REQUIRED)
        private String username;

        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        private String email;

        @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
        private String password;

        private String profileImage;

        private String bio;

    }

    @Getter
    @Setter
    @Builder
    public static class Response {

        private Long id;
        private String username;
        private String email;
        private String profileImage;
        private String bio;
        private LocalDateTime createdAt;

        public static ApiResponse<Response> fromEntity(User user) {
            Response response = Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profileImage(user.getProfileImage())
                    .bio(user.getBio())
                    .createdAt(user.getCreatedAt())
                    .build();

            return ApiResponse.<Response>builder()
                    .status(UserStatusEnum.OK.getStatusCode())
                    .message(UserStatusEnum.OK.getMessage())
                    .body(response)
                    .build();
        }
    }
}
