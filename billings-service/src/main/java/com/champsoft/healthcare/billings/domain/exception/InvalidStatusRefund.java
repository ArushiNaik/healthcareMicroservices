package com.champsoft.healthcare.billings.domain.exception;

public class InvalidStatusRefund extends RuntimeException {
    public InvalidStatusRefund(String message) {
        super(message);
    }
}
