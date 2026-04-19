package com.champsoft.healthcare.billings.domain.exception;

public class InvalidBillingException extends RuntimeException {
    public InvalidBillingException(String message) {
        super(message);
    }
}
