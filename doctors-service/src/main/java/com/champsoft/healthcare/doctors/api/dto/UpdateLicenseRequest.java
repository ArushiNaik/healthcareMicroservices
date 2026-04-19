package com.champsoft.healthcare.doctors.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateLicenseRequest {

    private LocalDate licenseExpiryDate;

}