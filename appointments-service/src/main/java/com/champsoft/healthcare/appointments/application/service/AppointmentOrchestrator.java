package com.champsoft.healthcare.appointments.application.service;

import com.champsoft.healthcare.appointments.api.dto.UpdateAppointmentRequest;
import com.champsoft.healthcare.appointments.application.port.out.*;
import com.champsoft.healthcare.appointments.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.champsoft.healthcare.appointments.domain.exception.AppointmentNotFoundException;
import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppointmentOrchestrator {

    private final AppointmentRepositoryPort repository;
    private final DoctorEligibilityPort doctorPort;
    private final PatientEligibilityPort patientPort;
    private final BillingPort billingPort;

    public AppointmentOrchestrator(AppointmentRepositoryPort repository,
                                   DoctorEligibilityPort doctorPort,
                                   PatientEligibilityPort patientPort,
                                   BillingPort billingPort) {
        this.repository = repository;
        this.doctorPort = doctorPort;
        this.patientPort = patientPort;
        this.billingPort = billingPort;
    }

//    public Appointment create(String doctorId, String patientId,String billingId, LocalDateTime time) {
//
//        if (!doctorPort.exists(doctorId)) throw new RuntimeException("Doctor not found");
//        if (!patientPort.exists(patientId)) throw new RuntimeException("Patient not found");
//
//        Appointment appt = new Appointment(
//                new AppointmentId(UUID.randomUUID().toString()),
//                new DoctorRef(doctorId),
//                new PatientRef(patientId),
//                new BillingRef(billingId),
//                new AppointmentTime(time)
//        );
//
//        return repository.save(appt);
//    }
//
//    @Transactional(readOnly = true)
//    public Appointment getById(String id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//    }
//    @Transactional
//    public Appointment complete(String id) {
//
//        Appointment appt = getById(id);
//        appt.complete();
//        repository.save(appt);
//       // billingPort.createBill(appt.id().value(), appt.patientIdValue());
//
//        return appt;
//    }
//
//    @Transactional(readOnly = true) // ⚡ optimized read
//    public List<Appointment> getAll() {
//        return repository.findAll();
//    }
//
//
//
//    public void delete(String id) {
//        repository.deleteById(id);
//    }
//
//    public Appointment reschedule(String id, LocalDateTime newTime) {
//
//        Appointment appt = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//
//        appt.reschedule(new AppointmentTime(newTime));
//
//        return repository.save(appt);
//    }
//
//    public Appointment update(String id, UpdateAppointmentRequest req) {
//
//        Appointment appt = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//
//        if (req.time != null) {
//            appt.reschedule(new AppointmentTime(req.time));
//        }
//
//        return repository.save(appt);
//    }

    public Appointment create(String doctorId, String patientId, String billingId, LocalDateTime time) {
        if (!doctorPort.exists(doctorId)) throw new CrossContextValidationException("Doctor not found: " + doctorId);
        if (!patientPort.exists(patientId)) throw new CrossContextValidationException("Patient not found: " + patientId);

        Appointment appt = new Appointment(
                new AppointmentId(UUID.randomUUID().toString()),
                new DoctorRef(doctorId),
                new PatientRef(patientId),
                new BillingRef(billingId),
                new AppointmentTime(time)
        );
        return repository.save(appt);
    }
    @Transactional(readOnly = true)
    public List<Appointment> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Appointment getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));
    }

//    public Appointment complete(String id) {
//        Appointment appt = getById(id);
//        appt.complete();
//        return repository.save(appt);
//    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new AppointmentNotFoundException("Appointment not found: " + id);
        }
        repository.deleteById(id);
    }

    public Appointment reschedule(String id, LocalDateTime newTime) {
        Appointment appt = repository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));
        appt.reschedule(new AppointmentTime(newTime));
        return repository.save(appt);
    }

    public Appointment update(String id, UpdateAppointmentRequest req) {
        Appointment appt = repository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));
        if (req.time != null) {
            appt.reschedule(new AppointmentTime(req.time));
        }
        return repository.save(appt);
    }
}