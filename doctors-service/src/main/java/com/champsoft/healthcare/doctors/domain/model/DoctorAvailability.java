package com.champsoft.healthcare.doctors.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a recurring availability slot for a doctor.
 */
public record DoctorAvailability(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {

    public DoctorAvailability {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }


    public boolean matches(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek() == dayOfWeek &&
                !dateTime.toLocalTime().isBefore(startTime) &&
                !dateTime.toLocalTime().isAfter(endTime);
    }
}