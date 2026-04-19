package com.champsoft.healthcare.patients.api.dto;

import com.champsoft.healthcare.patients.domain.model.PatientStatus;

public record PatientResponse(
        String id,
        String firstName,
        String lastName,
        PatientStatus status
) {
}
