package io.github.sebkaminski16.carrentaladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CarModelDtos {

    public record CarModelCreateRequest(
            @NotBlank @Size(min = 1, max = 120) String name,
            @NotNull Long brandId
    ) {}

    public record CarModelUpdateRequest(
            @NotBlank @Size(min = 1, max = 120) String name,
            @NotNull Long brandId
    ) {}

    public record CarModelDto(
            Long id,
            String name,
            Long brandId,
            String brandName
    ) {}
}
