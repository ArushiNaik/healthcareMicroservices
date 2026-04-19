package com.champsoft.healthcare.patients.domain.exception;

public class InvalidInsuranceCardNumber extends RuntimeException{
    public InvalidInsuranceCardNumber(String message) {
        super(message);
    }
}
