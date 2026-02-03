package io.github.sebkaminski16.carrentaladmin.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CategoryDtos {

    public record CategoryCreateRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 255) String description,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal dailyDiscountPercent,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal weeklyDiscountPercent
    ) {}

    public record CategoryUpdateRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 255) String description,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal dailyDiscountPercent,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal weeklyDiscountPercent
    ) {}

    public record CategoryDto(
            Long id,
            String name,
            String description,
            BigDecimal dailyDiscountPercent,
            BigDecimal weeklyDiscountPercent
    ) {}
}
