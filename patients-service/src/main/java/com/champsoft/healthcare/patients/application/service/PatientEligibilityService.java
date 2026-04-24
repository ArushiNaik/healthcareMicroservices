package com.champsoft.healthcare.patients.application.service;

import com.champsoft.healthcare.patients.application.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.application.port.out.PatientRepositoryPort;
import com.champsoft.healthcare.patients.domain.exception.PatientEligibilityAppointmentException;
import com.champsoft.healthcare.patients.domain.model.PatientId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientEligibilityService {


    private final PatientRepositoryPort repo;

    public PatientEligibilityService(@Qualifier("jpaPatientRepositoryAdapter") PatientRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligibleForAppointment(String id) {
        var patient = repo.findById(PatientId.of(id))
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (!patient.isEligibleForAppointment()) {
            throw new PatientEligibilityAppointmentException("Patient must be 18 or older to book an appointment");
        }

        return true;
    }
}
