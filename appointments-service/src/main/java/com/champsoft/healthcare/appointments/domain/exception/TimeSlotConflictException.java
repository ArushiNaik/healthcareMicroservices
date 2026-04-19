package com.champsoft.healthcare.appointments.domain.exception;

public class TimeSlotConflictException extends RuntimeException {
    public TimeSlotConflictException(String message) {
        super(message);
    }
}
