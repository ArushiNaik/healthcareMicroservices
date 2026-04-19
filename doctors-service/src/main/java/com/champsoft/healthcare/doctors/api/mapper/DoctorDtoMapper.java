package com.champsoft.healthcare.doctors.api.mapper;

import com.champsoft.healthcare.doctors.api.dto.CreateDoctorRequest;
import com.champsoft.healthcare.doctors.api.dto.DoctorResponse;
import com.champsoft.healthcare.doctors.domain.model.Doctor;

import java.util.UUID;

public class DoctorDtoMapper {

    public static Doctor toDomain(CreateDoctorRequest request) {

        return new Doctor(
                UUID.randomUUID().toString(),
                request.getFirstName(),
                request.getLastName(),
                request.getSpeciality(),
                request.getLicenseExpiryDate()
        );
    }

    public static DoctorResponse toResponse(Doctor doctor) {

        DoctorResponse response = new DoctorResponse();
        response.setId(String.valueOf(UUID.fromString(doctor.getId())));
        response.setFirstName(doctor.getFirstName());
        response.setLastName(doctor.getLastName());
        response.setSpeciality(doctor.getSpecialty());
        response.setActive(doctor.isActive());

        return response;
    }
}