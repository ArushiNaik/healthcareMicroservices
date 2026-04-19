package com.champsoft.healthcare.billings.domain.exception;

public class InvalidInvoiceItemException extends RuntimeException {
    public InvalidInvoiceItemException(String message) {
        super(message);
    }
}
