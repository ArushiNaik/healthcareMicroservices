package com.champsoft.healthcare.patients.domain.exception;

public class ExpiredHealthInsuranceCardException extends RuntimeException {
    public ExpiredHealthInsuranceCardException(String message) {
        super(message);
    }
}
