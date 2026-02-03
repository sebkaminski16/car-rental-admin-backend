package io.github.sebkaminski16.carrentaladmin.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
