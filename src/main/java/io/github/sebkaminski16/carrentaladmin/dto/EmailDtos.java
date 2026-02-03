package io.github.sebkaminski16.carrentaladmin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailDtos {

    public record EmailTestRequest(
            @NotBlank @Email String toEmail,
            @NotBlank @Size(max = 120) String subject,
            @NotBlank @Size(max = 5000) String text
    ) {}

    public record EmailSendResponse(
            boolean success
    ) {}
}
