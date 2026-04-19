package com.champsoft.healthcare.doctors.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDoctorRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String speciality
) {}