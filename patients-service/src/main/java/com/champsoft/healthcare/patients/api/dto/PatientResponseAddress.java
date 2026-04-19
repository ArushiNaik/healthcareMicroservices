package com.champsoft.healthcare.patients.api.dto;

import com.champsoft.healthcare.patients.domain.model.PatientStatus;

public record PatientResponseAddress (
        String id,
        String firstName,
        String lastName,
        Integer streetNumber,
        String streetName,
        String city,
        String postalCode,
        String Country,
        PatientStatus status
){
}
