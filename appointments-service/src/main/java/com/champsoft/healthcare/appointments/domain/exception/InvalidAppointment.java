package com.champsoft.healthcare.appointments.domain.exception;

public class InvalidAppointment extends RuntimeException {
    public InvalidAppointment(String message) {
        super(message);
    }
}
