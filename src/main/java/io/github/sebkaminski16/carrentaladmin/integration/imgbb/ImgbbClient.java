package io.github.sebkaminski16.carrentaladmin.integration.imgbb;

import io.github.sebkaminski16.carrentaladmin.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ImgbbClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.imgbb.apiKey}")
    private String apiKey;

    public String uploadBase64(String base64Image) {

        String baseUrl = "https://api.imgbb.com/1/upload?key=";
        String url = baseUrl + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);

        try {
            ResponseEntity<ImgbbUploadResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    ImgbbUploadResponse.class
            );

            ImgbbUploadResponse resp = response.getBody();
            if (resp == null || resp.data() == null) {
                throw new ExternalApiException("ImgBB upload failed: empty response body");
            }

            String finalUrl = resp.data().displayUrl() != null ? resp.data().displayUrl() : resp.data().url();
            if (finalUrl == null || finalUrl.isBlank()) {
                throw new ExternalApiException("ImgBB upload failed: missing URL in response");
            }

            return finalUrl;
        } catch (RestClientException ex) {
            throw new ExternalApiException("ImgBB upload request failed", ex);
        }
    }
}
