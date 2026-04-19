package com.champsoft.healthcare.patients.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        @Email String email,
        @Past LocalDate dateOfBirth,
        @NotBlank String healthCardNum,
        @NotNull LocalDate expiryDate,
        @NotNull Integer streetNumber,
        @NotBlank String streetName,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String Country
) {
}
