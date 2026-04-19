package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import org.springframework.stereotype.Component;

@Component
public class PatientEligibilityAdapter implements PatientEligibilityPort {
    @Override
    public boolean exists(String patientId) {
        return true;
    }
}