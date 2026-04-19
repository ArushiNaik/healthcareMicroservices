package com.champsoft.healthcare.appointments.api.dto;

import java.time.LocalDateTime;

public class AppointmentResponse {
    public String id;
    public String doctorId;
    public String patientId;
    public String billingId;
    public String status;
    public LocalDateTime time;

}