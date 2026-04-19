package com.champsoft.healthcare.appointments.api.mapper;

import com.champsoft.healthcare.appointments.api.dto.AppointmentResponse;
import com.champsoft.healthcare.appointments.domain.model.Appointment;

public class AppointmentRepresentationAssembler {

    public static AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse r = new AppointmentResponse();

        r.id = a.id().value();
        r.doctorId = a.doctorId().value();
        r.patientId = a.patientId().value();
        r.billingId=a.getBillingRef().value();
        r.status = a.status().name();
        r.time = a.time().value();

        return r;
    }
}