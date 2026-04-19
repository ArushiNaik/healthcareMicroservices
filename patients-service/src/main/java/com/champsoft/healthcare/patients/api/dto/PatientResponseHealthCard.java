package com.champsoft.healthcare.patients.api.dto;

import com.champsoft.healthcare.patients.domain.model.PatientStatus;

import java.time.LocalDate;

public record PatientResponseHealthCard(
        String id,
        String firstName,
        String lastName,
        String cardNum,
        LocalDate expiryDate,
        PatientStatus status
) {
}
