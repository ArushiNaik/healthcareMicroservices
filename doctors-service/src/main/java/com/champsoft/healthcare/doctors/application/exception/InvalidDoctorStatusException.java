package com.champsoft.healthcare.doctors.application.exception;

public class InvalidDoctorStatusException extends RuntimeException {
    public InvalidDoctorStatusException(String message) {
        super(message);
    }
}
