package com.champsoft.healthcare.appointments.application.service;

import com.champsoft.healthcare.appointments.application.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentsEligibilityService {

    private final AppointmentRepositoryPort repo;

    public AppointmentsEligibilityService(AppointmentRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligible(){
        return true;
    }
}
