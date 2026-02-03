package io.github.sebkaminski16.carrentaladmin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CustomerDtos {

    public record CustomerCreateRequest(
            @NotBlank @Size(max = 80) String firstName,
            @NotBlank @Size(max = 80) String lastName,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 40) String phone,
            @Size(max = 255) String address
    ) {}

    public record CustomerUpdateRequest(
            @NotBlank @Size(max = 80) String firstName,
            @NotBlank @Size(max = 80) String lastName,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 40) String phone,
            @Size(max = 255) String address
    ) {}

    public record CustomerDto(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
