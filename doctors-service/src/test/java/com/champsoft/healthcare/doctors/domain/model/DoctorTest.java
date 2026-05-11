package com.champsoft.healthcare.doctors.domain.model;

import com.champsoft.healthcare.doctors.domain.exception.DoctorLicenseExpiredException;
import com.champsoft.healthcare.doctors.domain.exception.InvalidDoctorException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class DoctorTest {

    private static final LocalDate FUTURE_LICENSE = LocalDate.now().plusYears(2);

    // Helper → creates a valid Doctor using a real UUID string
    private Doctor validDoctor() {
        return new Doctor(
                UUID.randomUUID().toString(),
                "Alice", "Brown", "Cardiology",
                FUTURE_LICENSE
        );
    }

    @Test
    void shouldCreateDoctorAsActiveByDefault() {

        // ------------------- Act -------------------
        // Business rule: a newly created Doctor starts as active.
        Doctor doctor = validDoctor();

        // ------------------- Assert -------------------
        assertThat(doctor.getFirstName()).isEqualTo("Alice");
        assertThat(doctor.getLastName()).isEqualTo("Brown");
        assertThat(doctor.getSpecialty()).isEqualTo("Cardiology");
        assertThat(doctor.getLicenseExpiryDate()).isEqualTo(FUTURE_LICENSE);

        // Business rule: new doctor is active by default.
        assertThat(doctor.isActive()).isTrue();

        // Business rule: license is valid (future expiry date).
        assertThat(doctor.isLicenseValid()).isTrue();
    }

    @Test
    void shouldDeactivateDoctorSuccessfully() {

        // ------------------- Arrange -------------------
        Doctor doctor = validDoctor();
        assertThat(doctor.isActive()).isTrue();

        // ------------------- Act -------------------
        doctor.deactivate();

        // ------------------- Assert -------------------
        assertThat(doctor.isActive()).isFalse();
    }

    @Test
    void shouldActivateDoctorSuccessfully() {

        // ------------------- Arrange -------------------
        Doctor doctor = validDoctor();
        doctor.deactivate();
        assertThat(doctor.isActive()).isFalse();

        // ------------------- Act -------------------
        doctor.activate();

        // ------------------- Assert -------------------
        assertThat(doctor.isActive()).isTrue();
    }

    @Test
    void shouldUpdateInfoSuccessfully() {

        // ------------------- Arrange -------------------
        Doctor doctor = validDoctor();

        // ------------------- Act -------------------
        doctor.updateInfo("Bob", "Martin", "Neurology");

        // ------------------- Assert -------------------
        assertThat(doctor.getFirstName()).isEqualTo("Bob");
        assertThat(doctor.getLastName()).isEqualTo("Martin");
        assertThat(doctor.getSpecialty()).isEqualTo("Neurology");
    }

    @Test
    void shouldUpdateLicenseSuccessfully() {

        // ------------------- Arrange -------------------
        Doctor doctor = validDoctor();
        LocalDate newExpiry = LocalDate.now().plusYears(5);

        // ------------------- Act -------------------
        doctor.updateLicense(newExpiry);

        // ------------------- Assert -------------------
        assertThat(doctor.getLicenseExpiryDate()).isEqualTo(newExpiry);
        assertThat(doctor.isLicenseValid()).isTrue();
    }

    @Test
    void shouldThrowInvalidDoctorExceptionWhenIdIsNull() {

        // ------------------- Act + Assert -------------------
        assertThrows(InvalidDoctorException.class,
                () -> new Doctor(null, "Alice", "Brown", "Cardiology", FUTURE_LICENSE));
    }

    @Test
    void shouldThrowInvalidDoctorExceptionWhenIdIsBlank() {

        // ------------------- Act + Assert -------------------
        assertThrows(InvalidDoctorException.class,
                () -> new Doctor("   ", "Alice", "Brown", "Cardiology", FUTURE_LICENSE));
    }

    @Test
    void shouldThrowInvalidDoctorExceptionWhenLicenseIsNull() {

        // ------------------- Act + Assert -------------------
        assertThrows(InvalidDoctorException.class,
                () -> new Doctor(UUID.randomUUID().toString(), "Alice", "Brown", "Cardiology", null));
    }

    @Test
    void shouldThrowDoctorLicenseExpiredExceptionWhenLicenseIsInThePast() {

        // ------------------- Arrange -------------------
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // ------------------- Act + Assert -------------------
        assertThrows(DoctorLicenseExpiredException.class,
                () -> new Doctor(UUID.randomUUID().toString(), "Alice", "Brown", "Cardiology", pastDate));
    }

    @Test
    void shouldThrowDoctorLicenseExpiredExceptionWhenUpdatingLicenseWithNull() {

        // ------------------- Arrange -------------------
        Doctor doctor = validDoctor();

        // ------------------- Act + Assert -------------------
        assertThrows(DoctorLicenseExpiredException.class,
                () -> doctor.updateLicense(null));
    }
}
