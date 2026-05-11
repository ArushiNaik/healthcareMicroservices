package com.champsoft.healthcare.doctors.application.service;

import com.champsoft.healthcare.doctors.application.exception.DoctorNotFoundException;
import com.champsoft.healthcare.doctors.application.port.out.DoctorRepositoryPort;
import com.champsoft.healthcare.doctors.domain.exception.DuplicateDoctorException;
import com.champsoft.healthcare.doctors.domain.model.Doctor;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Enable Mockito → pure service test (NO Spring, NO DB)
@ExtendWith(MockitoExtension.class)
class DoctorCrudServiceTest {

    @Mock
    private DoctorRepositoryPort repository;

    @InjectMocks
    private DoctorCrudService service;

    private static final LocalDate FUTURE_LICENSE = LocalDate.now().plusYears(2);

    // Helper → creates a valid Doctor. Uses UUID string for id (service calls UUID.fromString internally).
    private Doctor sampleDoctor() {
        String id = UUID.randomUUID().toString();
        return new Doctor(id, "Alice", "Brown", "Cardiology", FUTURE_LICENSE);
    }

    @Nested
    @DisplayName("Create doctor")
    class CreateDoctorTests {

        @Test
        void shouldCreateDoctorSuccessfully() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            // Doctor ID does not exist yet → creation allowed
            when(repository.existsById(doctor.getId())).thenReturn(false);
            when(repository.save(any(Doctor.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            // service.create(Doctor) takes a Doctor object directly
            Doctor saved = service.create(doctor);

            // ------------------- Assert -------------------
            assertThat(saved).isNotNull();
            assertThat(saved.getFirstName()).isEqualTo("Alice");
            // Business rule: new doctor is active by default
            assertThat(saved.isActive()).isTrue();

            verify(repository).existsById(doctor.getId());
            verify(repository).save(any(Doctor.class));
        }

        @Test
        void shouldThrowDuplicateDoctorExceptionWhenDoctorAlreadyExists() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            when(repository.existsById(doctor.getId())).thenReturn(true);

            // ------------------- Act + Assert -------------------
            assertThrows(DuplicateDoctorException.class,
                    () -> service.create(doctor));

            verify(repository).existsById(doctor.getId());
            verify(repository, never()).save(any(Doctor.class));
        }
    }

    @Nested
    @DisplayName("Read doctor")
    class ReadDoctorTests {

        @Test
        void shouldReturnDoctorWhenFoundById() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            when(repository.findById(doctor.getId()))
                    .thenReturn(Optional.of(doctor));

            // ------------------- Act -------------------
            Doctor found = service.getById(doctor.getId());

            // ------------------- Assert -------------------
            assertThat(found).isSameAs(doctor);
            verify(repository).findById(doctor.getId());
        }

        @Test
        void shouldThrowDoctorNotFoundExceptionWhenMissing() {

            // ------------------- Arrange -------------------
            String id = UUID.randomUUID().toString();
            when(repository.findById(id)).thenReturn(Optional.empty());

            // ------------------- Act + Assert -------------------
            assertThrows(DoctorNotFoundException.class,
                    () -> service.getById(id));
        }

        @Test
        void shouldReturnAllDoctors() {

            // ------------------- Arrange -------------------
            List<Doctor> doctors = List.of(sampleDoctor(), sampleDoctor());
            when(repository.findAll()).thenReturn(doctors);

            // ------------------- Act -------------------
            List<Doctor> result = service.getAll();

            // ------------------- Assert -------------------
            assertThat(result).hasSize(2);
            verify(repository).findAll();
        }
    }

    @Nested
    @DisplayName("Update doctor info")
    class UpdateDoctorInfoTests {

        @Test
        void shouldUpdateInfoSuccessfully() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            when(repository.findById(doctor.getId()))
                    .thenReturn(Optional.of(doctor));
            when(repository.save(any(Doctor.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Doctor updated = service.updateInfo(doctor.getId(), "Bob", "Martin", "Neurology");

            // ------------------- Assert -------------------
            assertThat(updated.getFirstName()).isEqualTo("Bob");
            assertThat(updated.getSpecialty()).isEqualTo("Neurology");
            verify(repository).save(doctor);
        }
    }

    @Nested
    @DisplayName("Update doctor license")
    class UpdateLicenseTests {

        @Test
        void shouldUpdateLicenseSuccessfully() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            LocalDate newExpiry = LocalDate.now().plusYears(5);
            when(repository.findById(doctor.getId()))
                    .thenReturn(Optional.of(doctor));
            when(repository.save(any(Doctor.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Doctor updated = service.updateLicense(doctor.getId(), newExpiry);

            // ------------------- Assert -------------------
            assertThat(updated.getLicenseExpiryDate()).isEqualTo(newExpiry);
            verify(repository).save(doctor);
        }
    }

    @Nested
    @DisplayName("Activate / Deactivate doctor")
    class ActivateDeactivateTests {

        @Test
        void shouldActivateDoctorSuccessfully() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            doctor.deactivate();
            assertThat(doctor.isActive()).isFalse();

            when(repository.findById(doctor.getId()))
                    .thenReturn(Optional.of(doctor));
            when(repository.save(any(Doctor.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Doctor activated = service.activate(doctor.getId());

            // ------------------- Assert -------------------
            assertThat(activated.isActive()).isTrue();
        }

        @Test
        void shouldDeactivateDoctorSuccessfully() {

            // ------------------- Arrange -------------------
            Doctor doctor = sampleDoctor();
            assertThat(doctor.isActive()).isTrue();

            when(repository.findById(doctor.getId()))
                    .thenReturn(Optional.of(doctor));
            when(repository.save(any(Doctor.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // ------------------- Act -------------------
            Doctor deactivated = service.deactivate(doctor.getId());

            // ------------------- Assert -------------------
            assertThat(deactivated.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Delete doctor")
    class DeleteDoctorTests {

        @Test
        void shouldDeleteDoctorSuccessfully() {

            // ------------------- Arrange -------------------
            UUID uuid = UUID.randomUUID();
            when(repository.existsById(uuid.toString())).thenReturn(true);

            // ------------------- Act -------------------
            // service.delete takes a UUID object
            service.delete(uuid);

            // ------------------- Assert -------------------
            verify(repository).existsById(uuid.toString());
            verify(repository).deleteById(uuid.toString());
        }

        @Test
        void shouldThrowDoctorNotFoundExceptionWhenDeletingMissingDoctor() {

            // ------------------- Arrange -------------------
            UUID uuid = UUID.randomUUID();
            when(repository.existsById(uuid.toString())).thenReturn(false);

            // ------------------- Act + Assert -------------------
            assertThrows(DoctorNotFoundException.class,
                    () -> service.delete(uuid));

            verify(repository, never()).deleteById(anyString());
        }
    }
}
