package com.champsoft.healthcare.doctors.application.service;

import com.champsoft.healthcare.doctors.application.port.out.DoctorRepositoryPort;
import com.champsoft.healthcare.doctors.domain.model.Doctor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Enable Mockito → pure service test (NO Spring, NO DB, NO HTTP)
@ExtendWith(MockitoExtension.class)
class DoctorEligibilityServiceTest {

    @Mock
    private DoctorRepositoryPort repo;

    @InjectMocks
    private DoctorEligibilityService service;

    private static final LocalDate FUTURE_LICENSE = LocalDate.now().plusYears(2);

    @Test
    void shouldReturnTrueWhenDoctorIsActiveAndLicenseIsValid() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        Doctor doctor = new Doctor(id, "Alice", "Brown", "Cardiology", FUTURE_LICENSE);
        // new Doctor starts active by default
        when(repo.findById(id)).thenReturn(Optional.of(doctor));

        // ------------------- Act -------------------
        // DoctorEligibilityService.isEligible calls UUID.fromString internally
        boolean result = service.isEligible(id);

        // ------------------- Assert -------------------
        assertThat(result).isTrue();
        verify(repo).findById(id);
    }

    @Test
    void shouldReturnFalseWhenDoctorIsInactive() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        Doctor doctor = new Doctor(id, "Bob", "Smith", "Neurology", FUTURE_LICENSE);
        doctor.deactivate();
        when(repo.findById(id)).thenReturn(Optional.of(doctor));

        // ------------------- Act -------------------
        boolean result = service.isEligible(id);

        // ------------------- Assert -------------------
        // Business rule: isLicenseValid() && isActive() → false when inactive
        assertThat(result).isFalse();
        verify(repo).findById(id);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDoctorNotFound() {

        // ------------------- Arrange -------------------
        // DoctorEligibilityService throws IllegalArgumentException (not DoctorNotFoundException)
        String id = UUID.randomUUID().toString();
        when(repo.findById(id)).thenReturn(Optional.empty());

        // ------------------- Act + Assert -------------------
        assertThrows(IllegalArgumentException.class,
                () -> service.isEligible(id));

        verify(repo).findById(id);
    }
}
