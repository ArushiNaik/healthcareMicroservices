package com.champsoft.healthcare.appointments.application.service;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.AppointmentRepositoryPort;
import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import com.champsoft.healthcare.appointments.domain.exception.AppointmentNotFoundException;
import com.champsoft.healthcare.appointments.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Enable Mockito → pure service test (NO Spring, NO DB, NO HTTP)
@ExtendWith(MockitoExtension.class)
class AppointmentOrchestratorTest {

    @Mock private AppointmentRepositoryPort repository;
    @Mock private DoctorEligibilityPort doctorPort;
    @Mock private PatientEligibilityPort patientPort;
    @Mock private BillingPort billingPort;

    @InjectMocks
    private AppointmentOrchestrator orchestrator;

    // Valid future appointment time
    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    // Helper → creates a valid Appointment domain object
    private Appointment sampleAppointment(String id) {
        return new Appointment(
                new AppointmentId(id),
                new DoctorRef("doctor-1"),
                new PatientRef("patient-1"),
                new BillingRef("billing-1"),
                new AppointmentTime(FUTURE)
        );
    }

    @Nested
    @DisplayName("Create appointment")
    class CreateTests {

        @Test
        void shouldCreateAppointmentWhenDoctorAndPatientAreEligible() {

            // ------------------- Arrange -------------------
            when(doctorPort.exists("doctor-1")).thenReturn(true);
            when(patientPort.exists("patient-1")).thenReturn(true);
            when(repository.save(any(Appointment.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Appointment created = orchestrator.create("doctor-1", "patient-1", "billing-1", FUTURE);

            // ------------------- Assert -------------------
            assertThat(created).isNotNull();
            assertThat(created.doctorIdValue()).isEqualTo("doctor-1");
            assertThat(created.patientIdValue()).isEqualTo("patient-1");
            assertThat(created.getBillingRef().value()).isEqualTo("billing-1");
            assertThat(created.status()).isEqualTo(AppointmentStatus.SCHEDULED);

            verify(doctorPort).exists("doctor-1");
            verify(patientPort).exists("patient-1");
            verify(repository).save(any(Appointment.class));
        }

        @Test
        void shouldThrowCrossContextValidationExceptionWhenDoctorNotEligible() {

            // ------------------- Arrange -------------------
            // Doctor exists port returns false → orchestrator throws exception
            when(doctorPort.exists("bad-doctor")).thenReturn(false);

            // ------------------- Act + Assert -------------------
            assertThrows(CrossContextValidationException.class,
                    () -> orchestrator.create("bad-doctor", "patient-1", "billing-1", FUTURE));

            // Verify → save must NOT be called
            verify(repository, never()).save(any(Appointment.class));
        }

        @Test
        void shouldThrowCrossContextValidationExceptionWhenPatientNotEligible() {

            // ------------------- Arrange -------------------
            when(doctorPort.exists("doctor-1")).thenReturn(true);
            when(patientPort.exists("bad-patient")).thenReturn(false);

            // ------------------- Act + Assert -------------------
            assertThrows(CrossContextValidationException.class,
                    () -> orchestrator.create("doctor-1", "bad-patient", "billing-1", FUTURE));

            // Verify → save must NOT be called
            verify(repository, never()).save(any(Appointment.class));
        }

        @Test
        void shouldThrowCrossContextValidationExceptionWhenDoctorPortThrows() {

            // ------------------- Arrange -------------------
            // The ACL adapter itself can throw CrossContextValidationException
            when(doctorPort.exists("doctor-down"))
                    .thenThrow(new CrossContextValidationException("Doctor service unavailable"));

            // ------------------- Act + Assert -------------------
            assertThrows(CrossContextValidationException.class,
                    () -> orchestrator.create("doctor-down", "patient-1", "billing-1", FUTURE));

            verify(repository, never()).save(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("Read appointment")
    class ReadTests {

        @Test
        void shouldReturnAppointmentWhenFoundById() {

            // ------------------- Arrange -------------------
            Appointment appt = sampleAppointment("appt-1");
            when(repository.findById("appt-1")).thenReturn(Optional.of(appt));

            // ------------------- Act -------------------
            Appointment found = orchestrator.getById("appt-1");

            // ------------------- Assert -------------------
            assertThat(found).isSameAs(appt);
            verify(repository).findById("appt-1");
        }

        @Test
        void shouldThrowAppointmentNotFoundExceptionWhenMissing() {

            // ------------------- Arrange -------------------
            when(repository.findById("missing")).thenReturn(Optional.empty());

            // ------------------- Act + Assert -------------------
            // orchestrator.getById throws domain.exception.AppointmentNotFoundException
            assertThrows(AppointmentNotFoundException.class,
                    () -> orchestrator.getById("missing"));
        }

        @Test
        void shouldReturnAllAppointments() {

            // ------------------- Arrange -------------------
            List<Appointment> list = List.of(
                    sampleAppointment("appt-1"),
                    sampleAppointment("appt-2")
            );
            when(repository.findAll()).thenReturn(list);

            // ------------------- Act -------------------
            List<Appointment> result = orchestrator.getAll();

            // ------------------- Assert -------------------
            assertThat(result).hasSize(2);
            verify(repository).findAll();
        }
    }

    @Nested
    @DisplayName("Reschedule appointment")
    class RescheduleTests {

        @Test
        void shouldRescheduleSuccessfully() {

            // ------------------- Arrange -------------------
            Appointment appt = sampleAppointment("appt-1");
            LocalDateTime newTime = LocalDateTime.now().plusDays(60);

            when(repository.findById("appt-1")).thenReturn(Optional.of(appt));
            when(repository.save(any(Appointment.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Appointment rescheduled = orchestrator.reschedule("appt-1", newTime);

            // ------------------- Assert -------------------
            assertThat(rescheduled.time().value()).isEqualTo(newTime);
            verify(repository).findById("appt-1");
            verify(repository).save(appt);
        }

        @Test
        void shouldThrowAppointmentNotFoundExceptionWhenReschedulingMissing() {

            // ------------------- Arrange -------------------
            when(repository.findById("missing")).thenReturn(Optional.empty());

            // ------------------- Act + Assert -------------------
            assertThrows(AppointmentNotFoundException.class,
                    () -> orchestrator.reschedule("missing", LocalDateTime.now().plusDays(1)));

            verify(repository, never()).save(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("Delete appointment")
    class DeleteTests {

        @Test
        void shouldDeleteAppointmentSuccessfully() {

            // ------------------- Arrange -------------------
            when(repository.existsById("appt-1")).thenReturn(true);

            // ------------------- Act -------------------
            orchestrator.delete("appt-1");

            // ------------------- Assert -------------------
            verify(repository).existsById("appt-1");
            verify(repository).deleteById("appt-1");
        }

        @Test
        void shouldThrowAppointmentNotFoundExceptionWhenDeletingMissing() {

            // ------------------- Arrange -------------------
            when(repository.existsById("missing")).thenReturn(false);

            // ------------------- Act + Assert -------------------
            assertThrows(AppointmentNotFoundException.class,
                    () -> orchestrator.delete("missing"));

            verify(repository, never()).deleteById(anyString());
        }
    }
}
