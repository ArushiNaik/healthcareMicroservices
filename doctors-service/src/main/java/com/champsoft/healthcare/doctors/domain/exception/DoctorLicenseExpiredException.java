package com.champsoft.healthcare.doctors.domain.exception;

public class DoctorLicenseExpiredException extends RuntimeException {
    public DoctorLicenseExpiredException(String message) {
        super(message);
    }
}