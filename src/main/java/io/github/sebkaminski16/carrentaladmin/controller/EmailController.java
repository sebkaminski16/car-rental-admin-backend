package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.EmailDtos;
import io.github.sebkaminski16.carrentaladmin.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/test")
    public ResponseEntity<EmailDtos.EmailSendResponse> sendTest(@Valid @RequestBody EmailDtos.EmailTestRequest req) {
        boolean ok = emailService.sendTestEmail(req.toEmail(), req.subject(), req.text());
        return ResponseEntity.ok(new EmailDtos.EmailSendResponse(ok));
    }
}
