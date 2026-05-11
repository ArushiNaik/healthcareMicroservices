package com.champsoft.healthcare.patients.application.service;

import com.champsoft.healthcare.patients.application.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.application.port.out.PatientRepositoryPort;
import com.champsoft.healthcare.patients.domain.exception.PatientEligibilityAppointmentException;
import com.champsoft.healthcare.patients.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Enable Mockito → pure service test (NO Spring, NO DB, NO HTTP)
@ExtendWith(MockitoExtension.class)
class PatientEligibilityServiceTest {

    @Mock
    private PatientRepositoryPort repo;

    @InjectMocks
    private PatientEligibilityService service;

    private static final LocalDate FUTURE_EXPIRY = LocalDate.now().plusYears(2);

    private Patient adultPatient() {
        return new Patient(
                PatientId.of("patient-1"),
                "Alice", "Brown", "514-555-0001", "alice@example.com",
                LocalDate.now().minusYears(30),
                new HealthInsuranceCard("ABCD12345678", FUTURE_EXPIRY),
                new Address(123, "Main St", "Montreal", "H1A1A1", "Canada"),
                PatientStatus.STABLE
        );
    }

    private Patient minorPatient() {
        return new Patient(
                PatientId.of("minor-1"),
                "Bob", "Young", "514-555-0002", "bob@example.com",
                LocalDate.now().minusYears(15),
                new HealthInsuranceCard("WXYZ12345678", FUTURE_EXPIRY),
                new Address(456, "Elm St", "Montreal", "H1A1A1", "Canada"),
                PatientStatus.STABLE
        );
    }

    @Test
    void shouldReturnTrueWhenPatientIsAdult() {

        // ------------------- Arrange -------------------
        when(repo.findById(PatientId.of("patient-1")))
                .thenReturn(Optional.of(adultPatient()));

        // ------------------- Act -------------------
        boolean result = service.isEligibleForAppointment("patient-1");

        // ------------------- Assert -------------------
        assertThat(result).isTrue();
        verify(repo).findById(PatientId.of("patient-1"));
    }

    @Test
    void shouldThrowPatientEligibilityAppointmentExceptionWhenPatientIsMinor() {

        // ------------------- Arrange -------------------
        when(repo.findById(PatientId.of("minor-1")))
                .thenReturn(Optional.of(minorPatient()));

        // ------------------- Act + Assert -------------------
        // Business rule: minors throw PatientEligibilityAppointmentException
        assertThrows(PatientEligibilityAppointmentException.class,
                () -> service.isEligibleForAppointment("minor-1"));

        verify(repo).findById(PatientId.of("minor-1"));
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenPatientDoesNotExist() {

        // ------------------- Arrange -------------------
        when(repo.findById(PatientId.of("missing")))
                .thenReturn(Optional.empty());

        // ------------------- Act + Assert -------------------
        assertThrows(PatientNotFoundException.class,
                () -> service.isEligibleForAppointment("missing"));

        verify(repo).findById(PatientId.of("missing"));
    }
}
