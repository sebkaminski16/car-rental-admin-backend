package io.github.sebkaminski16.carrentaladmin.dto;

import io.github.sebkaminski16.carrentaladmin.entity.CarStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CarDtos {

    public record CarCreateRequest(
            @NotBlank @Size(max = 32) String vin,
            @NotBlank @Size(max = 16) String licensePlate,
            @NotNull @Min(1950) @Max(2100) Integer productionYear,
            @Size(max = 40) String color,
            @NotNull Long modelId,
            @NotNull Long categoryId,
            @Size(max = 512) String imageUrl,
            @NotNull @Positive BigDecimal hourlyRate,
            @NotNull @Positive BigDecimal dailyRate,
            @NotNull @Positive BigDecimal weeklyRate,
            @NotNull @Min(0) Integer mileageKm
    ) {}

    public record CarUpdateRequest(
            @NotBlank @Size(max = 32) String vin,
            @NotBlank @Size(max = 16) String licensePlate,
            @NotNull @Min(1950) @Max(2100) Integer productionYear,
            @Size(max = 40) String color,
            @NotNull Long modelId,
            @NotNull Long categoryId,
            @Size(max = 512) String imageUrl,
            @NotNull @Positive BigDecimal hourlyRate,
            @NotNull @Positive BigDecimal dailyRate,
            @NotNull @Positive BigDecimal weeklyRate,
            @NotNull @Min(0) Integer mileageKm,
            @NotNull CarStatus status
    ) {}

    public record CarDto(
            Long id,
            String vin,
            String licensePlate,
            Integer productionYear,
            String color,
            CarStatus status,
            Long modelId,
            String modelName,
            Long brandId,
            String brandName,
            Long categoryId,
            String categoryName,
            String imageUrl,
            BigDecimal hourlyRate,
            BigDecimal dailyRate,
            BigDecimal weeklyRate,
            Integer mileageKm
    ) {}
}
