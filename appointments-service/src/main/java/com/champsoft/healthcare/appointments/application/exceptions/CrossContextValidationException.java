package com.champsoft.healthcare.appointments.application.exceptions;

public class CrossContextValidationException extends RuntimeException {
    public CrossContextValidationException(String message) {
        super(message);
    }
}
