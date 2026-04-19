package com.champsoft.healthcare.doctors.domain.model;

import java.util.UUID;

public record DoctorId(UUID value) {
    public DoctorId {
        if (value == null) throw new IllegalArgumentException("DoctorId cannot be null");
    }

    public static DoctorId newId() {
        return new DoctorId(UUID.randomUUID());
    }
}