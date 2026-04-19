package com.champsoft.healthcare.appointments.application.port.out;

public interface DoctorEligibilityPort {
    boolean exists(String doctorId);
}