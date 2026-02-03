package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
public class EmailControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    public void testSendTestEmailSuccessfully() throws Exception {
        //given
        when(emailService.sendTestEmail("test@example.com", "Test Subject", "Test message"))
                .thenReturn(true);

        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"Test Subject\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(emailService, times(1)).sendTestEmail("test@example.com", "Test Subject", "Test message");
    }

    @Test
    public void testSendTestEmailWhenServiceReturnsFalse() throws Exception {
        //given
        when(emailService.sendTestEmail("test@example.com", "Test Subject", "Test message"))
                .thenReturn(false);

        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"Test Subject\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)));

        verify(emailService, times(1)).sendTestEmail("test@example.com", "Test Subject", "Test message");
    }

    @Test
    public void testSendTestEmailWithBlankToEmailReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"toEmail\":\"\",\"subject\":\"Test Subject\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithInvalidEmailReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"toEmail\":\"invalid-email\",\"subject\":\"Test Subject\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithBlankSubjectReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithBlankTextReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"Test Subject\",\"text\":\"\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithTooLongSubjectReturnsValidationError() throws Exception {
        //given
        String longSubject = "A".repeat(121);
        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"" + longSubject + "\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithTooLongTextReturnsValidationError() throws Exception {
        //given
        String longText = "A".repeat(5001);
        String requestBody = "{\"toEmail\":\"test@example.com\",\"subject\":\"Test Subject\",\"text\":\"" + longText + "\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendTestEmailWithNullToEmailReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"toEmail\":null,\"subject\":\"Test Subject\",\"text\":\"Test message\"}";
        //when&then
        mockMvc.perform(post("/api/emails/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTestEmail(anyString(), anyString(), anyString());
    }
}