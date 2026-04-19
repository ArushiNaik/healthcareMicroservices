package com.champsoft.healthcare.doctors.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DoctorResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String speciality;
    private boolean active;

}