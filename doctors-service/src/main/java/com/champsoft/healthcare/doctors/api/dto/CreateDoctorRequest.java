package com.champsoft.healthcare.doctors.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class CreateDoctorRequest {
    private String firstName;
    private String lastName;
    private String speciality;
    private LocalDate licenseExpiryDate;

}