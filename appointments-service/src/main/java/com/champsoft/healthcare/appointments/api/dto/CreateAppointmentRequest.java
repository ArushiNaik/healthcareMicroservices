package com.champsoft.healthcare.appointments.api.dto;

import java.time.LocalDateTime;

public class CreateAppointmentRequest {
    public String doctorId;
    public String patientId;
    public String billingId;
    public LocalDateTime time;
}