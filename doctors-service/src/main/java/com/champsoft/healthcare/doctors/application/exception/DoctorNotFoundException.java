package com.champsoft.healthcare.doctors.application.exception;

public class DoctorNotFoundException extends RuntimeException{
    public DoctorNotFoundException(String message) {
        super(message);
    }
}

