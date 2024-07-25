package com.example.carrotmarketbackend.Post;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;

    @NotNull
    private String authorId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;

    @DecimalMin(value = "-90.000000")
    @DecimalMax(value = "90.000000")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.000000")
    @DecimalMax(value = "180.000000")
    private BigDecimal longitude;

    private String image;
}
