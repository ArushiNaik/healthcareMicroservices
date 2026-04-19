package com.champsoft.healthcare.appointments.application.port.out;

public interface BillingPort {
    void createBill(String appointmentId, String patientId);
}