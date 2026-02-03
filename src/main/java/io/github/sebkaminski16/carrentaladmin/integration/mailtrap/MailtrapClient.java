package io.github.sebkaminski16.carrentaladmin.integration.mailtrap;

import io.github.sebkaminski16.carrentaladmin.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class MailtrapClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.email.apiToken}")
    private String apiToken;

    @Value("${spring.email.inboxId}")
    private Long inboxId;

    public boolean sendTextEmail(String fromEmail, String toEmail, String subject, String text) {

        String baseUrl = "https://sandbox.api.mailtrap.io";
        String url = normalizeBaseUrl(baseUrl) + "/api/send/" + inboxId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);
        headers.add("Api-Token", apiToken);

        Map<String, Object> payload = Map.of(
                "from", Map.of("email", fromEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "text", text
        );

        try {
            ResponseEntity<MailtrapSendResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    MailtrapSendResponse.class
            );

            MailtrapSendResponse body = response.getBody();
            return response.getStatusCode().is2xxSuccessful() && body != null && Boolean.TRUE.equals(body.success());
        } catch (RestClientException ex) {
            throw new ExternalApiException("Failed to send email via Mailtrap Sandbox API", ex);
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }
}
