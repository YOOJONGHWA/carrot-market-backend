package com.example.carrotmarketbackend.User;


import com.example.carrotmarketbackend.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = ValidationMessages.NAME_REQUIRED)
    private String username;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(min = 6, message = ValidationMessages.PASSWORD_MIN_LENGTH)
    private String password;

    private String profileImage;

    private String bio;

    @NotNull(message = ValidationMessages.INTERNAL_SERVER_ERROR)
    private LocalDateTime createdAt;
}
