package com.champsoft.healthcare.doctors.domain.model;

import com.champsoft.healthcare.doctors.domain.exception.DoctorLicenseExpiredException;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Doctor {

    private final String id;

    private String firstName;
    private String lastName;
    private String specialty;
    private LocalDate licenseExpiryDate;
    private boolean active;

    private final List<DoctorAvailability> availabilities = new ArrayList<>();

    public Doctor(String id,
                  String firstName,
                  String lastName,
                  String specialty,
                  LocalDate licenseExpiryDate) {

        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (licenseExpiryDate == null) throw new IllegalArgumentException("license required");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.licenseExpiryDate = licenseExpiryDate;
        this.active = true;
    }

    public void updateInfo(String firstName, String lastName, String specialty) {
        if (firstName != null) this.firstName = firstName;
        if (lastName != null) this.lastName = lastName;
        if (specialty != null) this.specialty = specialty;
    }

    public void updateLicense(LocalDate expiryDate) {
        if (expiryDate == null) throw new IllegalArgumentException("expiry required");
        this.licenseExpiryDate = expiryDate;
    }

    public void activate() { this.active = true; }

    public void deactivate() { this.active = false; }

    public boolean isLicenseValid() {
        return licenseExpiryDate != null &&
                !licenseExpiryDate.isBefore(LocalDate.now());
    }

    public void validateLicense() {
        if (!isLicenseValid()) {
            throw new DoctorLicenseExpiredException("License expired");
        }
    }
}