package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import org.springframework.stereotype.Component;

@Component
public class DoctorEligibilityAdapter implements DoctorEligibilityPort {
    @Override
    public boolean exists(String doctorId) {
        return true; // replace with real repo call
    }
}