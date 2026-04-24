package com.champsoft.healthcare.doctors.domain.exception;

public class InvalidDoctorException extends RuntimeException {
    public InvalidDoctorException(String message) {
        super(message);
    }
}
