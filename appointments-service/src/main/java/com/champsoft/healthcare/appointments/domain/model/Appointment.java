package com.champsoft.healthcare.appointments.domain.model;

import com.champsoft.healthcare.appointments.domain.exception.InvalidAppointment;

import java.time.LocalDateTime;

public class Appointment {

    private final AppointmentId id;
    private final DoctorRef doctorId;
    private final PatientRef patientId;
    private final BillingRef billingRef;
    private AppointmentTime time;
    private AppointmentStatus status;


    public Appointment(AppointmentId id,
                       DoctorRef doctorId,
                       PatientRef patientId,
                       BillingRef billingId,
                       AppointmentTime time) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.billingRef= billingId;
        this.time = time;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public BillingRef getBillingRef() {
        return billingRef;
    }

    public AppointmentId id() { return id; }
    public DoctorRef doctorId() { return doctorId; }
    public PatientRef patientId() { return patientId; }
    public AppointmentTime time() { return time; }
    public AppointmentStatus status() { return status; }

    public String doctorIdValue() { return doctorId.value(); }
    public String patientIdValue() { return patientId.value(); }
    public LocalDateTime timeValue() { return time.value(); }

//    public void complete() {
//        if (time.value().isAfter(LocalDateTime.now())){
//            throw new InvalidAppointment("Appointment cannot be completed before date of the appointment");
//        }
//        this.status = AppointmentStatus.COMPLETED;
//    }
    public void cancel() { this.status = AppointmentStatus.CANCELLED; }
    public void reschedule(AppointmentTime newTime) {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Only scheduled appointments can be rescheduled");
        }
        this.time = newTime;
    }
}