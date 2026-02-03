package io.github.sebkaminski16.carrentaladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BrandDtos {

    public record BrandCreateRequest(
            @NotBlank @Size(min = 1, max = 120) String name
    ) {}

    public record BrandUpdateRequest(
            @NotBlank @Size(min = 1, max = 120) String name
    ) {}

    public record BrandDto(
            Long id,
            String name
    ) {}
}
