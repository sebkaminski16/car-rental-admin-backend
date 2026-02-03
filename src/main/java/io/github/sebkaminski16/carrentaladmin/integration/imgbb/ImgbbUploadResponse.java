package io.github.sebkaminski16.carrentaladmin.integration.imgbb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImgbbUploadResponse(
        ImgbbData data,
        Boolean success,
        Integer status
) {
    public record ImgbbData(
            String url,
            @JsonProperty("display_url") String displayUrl
    ) {}
}
