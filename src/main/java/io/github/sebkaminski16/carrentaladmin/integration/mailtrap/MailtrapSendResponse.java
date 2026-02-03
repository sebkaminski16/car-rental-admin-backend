package io.github.sebkaminski16.carrentaladmin.integration.mailtrap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MailtrapSendResponse(
        Boolean success,
        @JsonProperty("message_ids") List<String> messageIds
) {}
