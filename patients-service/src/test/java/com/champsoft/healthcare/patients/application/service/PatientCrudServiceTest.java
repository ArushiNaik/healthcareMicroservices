package com.champsoft.healthcare.patients.application.service;

import com.champsoft.healthcare.patients.application.exception.DuplicatePatientException;
import com.champsoft.healthcare.patients.application.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.application.port.out.PatientRepositoryPort;
import com.champsoft.healthcare.patients.domain.exception.PatientEligibilityAppointmentException;
import com.champsoft.healthcare.patients.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Enable Mockito → this is a pure service test (NO Spring, NO DB)
@ExtendWith(MockitoExtension.class)
class PatientCrudServiceTest {

    @Mock
    private PatientRepositoryPort repo;

    @InjectMocks
    private PatientCrudService service;

    private static final LocalDate FUTURE_EXPIRY = LocalDate.now().plusYears(2);
    private static final LocalDate ADULT_DOB = LocalDate.now().minusYears(30);
    private static final LocalDate MINOR_DOB = LocalDate.now().minusYears(15);

    // Helper → creates a valid HealthInsuranceCard
    private HealthInsuranceCard card(String raw) {
        return new HealthInsuranceCard(raw, FUTURE_EXPIRY);
    }

    // Helper → creates a valid Address
    private Address address() {
        return new Address(123, "Main Street", "Montreal", "H1A1A1", "Canada");
    }

    // Helper → creates a valid Patient
    private Patient samplePatient(String id) {
        return new Patient(
                PatientId.of(id),
                "Alice", "Brown",
                "514-555-0001", "alice@example.com",
                ADULT_DOB,
                card("ABCD12345678"),
                address(),
                PatientStatus.STABLE
        );
    }

    @Nested
    @DisplayName("Create patient")
    class CreatePatientTests {

        @Test
        void shouldCreatePatientSuccessfully() {

            // ------------------- Arrange -------------------
            // Card number formatted by constructor: "ABCD 1234 5678"
            when(repo.existsByInsuranceCard("ABCD 1234 5678")).thenReturn(false);
            when(repo.save(any(Patient.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Patient saved = service.create(
                    "Alice", "Brown", "514-555-0001", "alice@example.com",
                    ADULT_DOB, card("ABCD12345678"), address(), PatientStatus.STABLE);

            // ------------------- Assert -------------------
            assertThat(saved).isNotNull();
            assertThat(saved.firstName()).isEqualTo("Alice");
            assertThat(saved.status()).isEqualTo(PatientStatus.STABLE);

            verify(repo).existsByInsuranceCard("ABCD 1234 5678");
            verify(repo).save(any(Patient.class));
        }

        @Test
        void shouldThrowDuplicatePatientExceptionWhenCardAlreadyExists() {

            // ------------------- Arrange -------------------
            when(repo.existsByInsuranceCard("ABCD 1234 5678")).thenReturn(true);

            // ------------------- Act + Assert -------------------
            assertThrows(DuplicatePatientException.class,
                    () -> service.create(
                            "Alice", "Brown", "514-555-0001", "alice@example.com",
                            ADULT_DOB, card("ABCD12345678"), address(), PatientStatus.STABLE));

            // Verify → no save should happen
            verify(repo, never()).save(any(Patient.class));
        }

        @Test
        void shouldThrowPatientEligibilityAppointmentExceptionWhenPatientIsMinor() {

            // ------------------- Act + Assert -------------------
            // Business rule: patient must be 18+ to register.
            assertThrows(PatientEligibilityAppointmentException.class,
                    () -> service.create(
                            "Bob", "Young", "514-555-0002", "bob@example.com",
                            MINOR_DOB, card("WXYZ12345678"), address(), PatientStatus.STABLE));

            // Verify → no DB check or save should happen
            verify(repo, never()).existsByInsuranceCard(anyString());
            verify(repo, never()).save(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("Read patient")
    class ReadPatientTests {

        @Test
        void shouldReturnPatientWhenFoundById() {

            // ------------------- Arrange -------------------
            Patient patient = samplePatient("patient-1");
            when(repo.findById(PatientId.of("patient-1")))
                    .thenReturn(Optional.of(patient));

            // ------------------- Act -------------------
            Patient found = service.findById("patient-1");

            // ------------------- Assert -------------------
            assertThat(found).isSameAs(patient);
            verify(repo).findById(PatientId.of("patient-1"));
        }

        @Test
        void shouldThrowPatientNotFoundExceptionWhenMissing() {

            // ------------------- Arrange -------------------
            when(repo.findById(PatientId.of("missing")))
                    .thenReturn(Optional.empty());

            // ------------------- Act + Assert -------------------
            assertThrows(PatientNotFoundException.class,
                    () -> service.findById("missing"));
        }

        @Test
        void shouldReturnAllPatients() {

            // ------------------- Arrange -------------------
            List<Patient> patients = List.of(
                    samplePatient("p1"), samplePatient("p2"));
            when(repo.findAll()).thenReturn(patients);

            // ------------------- Act -------------------
            List<Patient> result = service.list();

            // ------------------- Assert -------------------
            assertThat(result).hasSize(2);
            verify(repo).findAll();
        }
    }

    @Nested
    @DisplayName("Update patient")
    class UpdatePatientTests {

        @Test
        void shouldUpdateInsuranceCardSuccessfully() {

            // ------------------- Arrange -------------------
            Patient patient = samplePatient("patient-1");
            when(repo.findById(PatientId.of("patient-1")))
                    .thenReturn(Optional.of(patient));
            when(repo.save(any(Patient.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Patient updated = service.updatePatientCard("patient-1", "WXYZ87654321", FUTURE_EXPIRY);

            // ------------------- Assert -------------------
            assertThat(updated.insuranceCard().insuranceCardNumber()).isEqualTo("WXYZ 8765 4321");
            verify(repo).save(patient);
        }

        @Test
        void shouldUpdateAddressSuccessfully() {

            // ------------------- Arrange -------------------
            Patient patient = samplePatient("patient-1");
            when(repo.findById(PatientId.of("patient-1")))
                    .thenReturn(Optional.of(patient));
            when(repo.save(any(Patient.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Patient updated = service.updateAddress(
                    "patient-1", 456, "Elm St", "Quebec City", "G1A1A1", "Canada");

            // ------------------- Assert -------------------
            assertThat(updated.getAddress().getCity()).isEqualTo("Quebec City");
            verify(repo).save(patient);
        }
    }

    @Nested
    @DisplayName("Change patient status")
    class ChangeStatusTests {

        @Test
        void shouldChangeStatusSuccessfully() {

            // ------------------- Arrange -------------------
            Patient patient = samplePatient("patient-1");
            assertThat(patient.status()).isEqualTo(PatientStatus.STABLE);
            when(repo.findById(PatientId.of("patient-1")))
                    .thenReturn(Optional.of(patient));
            when(repo.save(any(Patient.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Patient updated = service.changeStatus("patient-1", PatientStatus.CRITICAL);

            // ------------------- Assert -------------------
            assertThat(updated.status()).isEqualTo(PatientStatus.CRITICAL);
        }
    }

    @Nested
    @DisplayName("Delete patient")
    class DeletePatientTests {

        @Test
        void shouldDeletePatientSuccessfully() {

            // ------------------- Arrange -------------------
            Patient patient = samplePatient("patient-1");
            when(repo.findById(PatientId.of("patient-1")))
                    .thenReturn(Optional.of(patient));

            // ------------------- Act -------------------
            service.delete("patient-1");

            // ------------------- Assert -------------------
            verify(repo).findById(PatientId.of("patient-1"));
            verify(repo).deleteById(PatientId.of("patient-1"));
        }

        @Test
        void shouldThrowPatientNotFoundExceptionWhenDeletingMissingPatient() {

            // ------------------- Arrange -------------------
            when(repo.findById(PatientId.of("missing")))
                    .thenReturn(Optional.empty());

            // ------------------- Act + Assert -------------------
            assertThrows(PatientNotFoundException.class,
                    () -> service.delete("missing"));

            verify(repo, never()).deleteById(any(PatientId.class));
        }
    }
}
