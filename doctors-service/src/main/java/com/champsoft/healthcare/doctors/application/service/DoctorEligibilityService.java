package com.champsoft.healthcare.doctors.application.service;

import com.champsoft.healthcare.doctors.application.port.out.DoctorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DoctorEligibilityService{


    private final DoctorRepositoryPort repo;

    public DoctorEligibilityService(DoctorRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligible(String doctorId) {
        var d = repo.findById(String.valueOf(UUID.fromString(doctorId)))
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        return d.isLicenseValid() && d.isActive();
    }
}



