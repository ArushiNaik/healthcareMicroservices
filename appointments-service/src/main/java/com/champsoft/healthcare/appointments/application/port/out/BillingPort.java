package com.champsoft.healthcare.appointments.application.port.out;

public interface BillingPort {
    boolean exists(String billingId);
   // void createBill(String appointmentId, String patientId);
}