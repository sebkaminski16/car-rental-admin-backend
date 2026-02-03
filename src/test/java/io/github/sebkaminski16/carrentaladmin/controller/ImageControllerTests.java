package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.ImageDtos;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.ExternalApiException;
import io.github.sebkaminski16.carrentaladmin.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    public void testUploadImageSuccessfully() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        ImageDtos.ImageUploadResponse response = new ImageDtos.ImageUploadResponse(
                "https://example.com/image.jpg",
                "imgbb"
        );
        when(imageService.uploadCarImage(any())).thenReturn(response);
        //when&then
        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url", is("https://example.com/image.jpg")))
                .andExpect(jsonPath("$.provider", is("imgbb")));

        verify(imageService, times(1)).uploadCarImage(any());
    }

    @Test
    public void testUploadImageWhenFileIsEmptyReturnsBadRequest() throws Exception {
        //given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );
        when(imageService.uploadCarImage(any()))
                .thenThrow(new BadRequestException("File is required"));
        //when&then
        mockMvc.perform(multipart("/api/images/upload").file(emptyFile))
                .andExpect(status().isBadRequest());

        verify(imageService, times(1)).uploadCarImage(any());
    }

    @Test
    public void testUploadImageWhenExternalApiFailsReturnsBadGateway() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        when(imageService.uploadCarImage(any()))
                .thenThrow(new ExternalApiException("Failed to read uploaded file"));
        //when&then
        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isBadGateway());

        verify(imageService, times(1)).uploadCarImage(any());
    }

    @Test
    public void testUploadImageWithDifferentFileType() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test png content".getBytes()
        );
        ImageDtos.ImageUploadResponse response = new ImageDtos.ImageUploadResponse(
                "https://example.com/image.png",
                "imgbb"
        );
        when(imageService.uploadCarImage(any())).thenReturn(response);
        //when&then
        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url", is("https://example.com/image.png")))
                .andExpect(jsonPath("$.provider", is("imgbb")));

        verify(imageService, times(1)).uploadCarImage(any());
    }

    @Test
    public void testUploadImageWithLargeFile() throws Exception {
        //given
        byte[] largeContent = new byte[1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );
        ImageDtos.ImageUploadResponse response = new ImageDtos.ImageUploadResponse(
                "https://example.com/large.jpg",
                "imgbb"
        );
        when(imageService.uploadCarImage(any())).thenReturn(response);
        //when&then
        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url", is("https://example.com/large.jpg")));

        verify(imageService, times(1)).uploadCarImage(any());
    }
}