package io.github.sebkaminski16.carrentaladmin.dto;

public class ImageDtos {

    public record ImageUploadResponse(
            String url,
            String provider
    ) {}
}
