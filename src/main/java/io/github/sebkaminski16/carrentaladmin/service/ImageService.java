package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.ImageDtos;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.ExternalApiException;
import io.github.sebkaminski16.carrentaladmin.integration.imgbb.ImgbbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {

    @Autowired
    private ImgbbClient imgbbClient;

    public ImageDtos.ImageUploadResponse uploadCarImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String url = imgbbClient.uploadBase64(base64);
            return new ImageDtos.ImageUploadResponse(url, "imgbb");
        } catch (IOException ex) {
            throw new ExternalApiException("Failed to read uploaded file", ex);
        }
    }
}
