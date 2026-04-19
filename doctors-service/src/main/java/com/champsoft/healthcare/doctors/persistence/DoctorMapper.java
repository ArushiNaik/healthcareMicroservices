package com.champsoft.healthcare.doctors.persistence;

import com.champsoft.healthcare.doctors.domain.model.Doctor;

import java.util.UUID;

public class DoctorMapper {

    public static DoctorJpaEntity toEntity(Doctor doctor) {

        DoctorJpaEntity e = new DoctorJpaEntity();

        e.setId(String.valueOf(UUID.fromString(doctor.getId().toString())));
        e.setFirstName(doctor.getFirstName());
        e.setLastName(doctor.getLastName());
        e.setSpecialty(doctor.getSpecialty());
        e.setLicenseExpiryDate(doctor.getLicenseExpiryDate());
        e.setActive(doctor.isActive());

        return e;
    }

    public static Doctor toDomain(DoctorJpaEntity e) {

        Doctor doctor = new Doctor(
                e.getId().toString(),
                e.getFirstName(),
                e.getLastName(),
                e.getSpecialty(),
                e.getLicenseExpiryDate()
        );

        if (!e.isActive()) {
            doctor.deactivate();
        }

        return doctor;
    }
}