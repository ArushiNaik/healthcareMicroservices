package com.champsoft.healthcare.appointments.application.port.out;

public interface PatientEligibilityPort {
    boolean exists(String patientId);
}