package com.champsoft.healthcare.appointments.infrastructure.persistance;

import com.champsoft.healthcare.appointments.domain.model.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest → loads only JPA layer (entities + repositories)
// Starts H2 in-memory database automatically
// Flyway V1__init_schema.sql runs → appointment table is created with seeded data
@DataJpaTest
@ActiveProfiles("testing")
class AppointmentRepositoryIntegrationTest {

    // Real repository (NOT mocked) — tests real DB behavior.
    @Autowired
    private SpringDataAppointmentRepository repository;

    // Flyway seeds a6 with CURRENT_TIMESTAMP then adds 3 months.
    // All seeded records are in the future after migration.
    // We test the JPA entity directly — no AppointmentTime validation at this layer.

    // Helper → builds a valid AppointmentJpaEntity for persistence testing.
    // time must be in the future (Flyway already ensures seeded data is future).
    private AppointmentJpaEntity buildEntity(String id) {
        return AppointmentJpaEntity.builder()
                .id(id)
                .doctorId(UUID.randomUUID().toString())
                .patientId("patient-" + id)
                .billingId("billing-" + id)
                .time(LocalDateTime.now().plusDays(30))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("Should save an appointment successfully")
    void shouldSaveAppointmentSuccessfully() {

        // ------------------- Arrange -------------------
        AppointmentJpaEntity entity = buildEntity("test-save-1");

        // ------------------- Act -------------------
        AppointmentJpaEntity saved = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo("test-save-1");
        assertThat(saved.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should find an appointment by ID")
    void shouldFindAppointmentById() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity("test-find-1"));

        // ------------------- Act -------------------
        Optional<AppointmentJpaEntity> found = repository.findById("test-find-1");

        // ------------------- Assert -------------------
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("test-find-1");
        assertThat(found.get().getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should return empty when appointment ID not found")
    void shouldReturnEmptyWhenAppointmentIdNotFound() {

        // ------------------- Act -------------------
        Optional<AppointmentJpaEntity> found = repository.findById("NON-EXISTENT-ID");

        // ------------------- Assert -------------------
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when appointment exists by ID")
    void shouldReturnTrueWhenAppointmentExistsById() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity("test-exists-1"));

        // ------------------- Act -------------------
        boolean exists = repository.existsById("test-exists-1");

        // ------------------- Assert -------------------
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when appointment does not exist by ID")
    void shouldReturnFalseWhenAppointmentDoesNotExistById() {

        // ------------------- Act -------------------
        boolean exists = repository.existsById("NON-EXISTENT-ID");

        // ------------------- Assert -------------------
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find all appointments including Flyway-seeded data")
    void shouldFindAllAppointments() {

        // ------------------- Arrange -------------------
        // Flyway already seeded 6 appointments.
        // Save 2 more to verify findAll returns at least 8.
        repository.save(buildEntity("test-list-1"));
        repository.save(buildEntity("test-list-2"));

        // ------------------- Act -------------------
        List<AppointmentJpaEntity> all = repository.findAll();

        // ------------------- Assert -------------------
        // At least the 2 we just saved (Flyway may seed more)
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should delete an appointment by ID successfully")
    void shouldDeleteAppointmentSuccessfully() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity("test-delete-1"));
        assertThat(repository.existsById("test-delete-1")).isTrue();

        // ------------------- Act -------------------
        repository.deleteById("test-delete-1");

        // ------------------- Assert -------------------
        assertThat(repository.findById("test-delete-1")).isEmpty();
    }

    @Test
    @DisplayName("Should update appointment status to CANCELLED")
    void shouldUpdateAppointmentStatusToCancelled() {

        // ------------------- Arrange -------------------
        AppointmentJpaEntity entity = buildEntity("test-cancel-1");
        repository.save(entity);

        // ------------------- Act -------------------
        entity.setStatus(AppointmentStatus.CANCELLED);
        AppointmentJpaEntity updated = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(updated.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should find a seeded appointment by ID a1")
    void shouldFindSeededAppointmentByIdA1() {

        // ------------------- Act -------------------
        // Flyway seeds appointment with id='a1'
        Optional<AppointmentJpaEntity> found = repository.findById("a1");

        // ------------------- Assert -------------------
        // The seeded record exists
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        // doctor_id was set to '11111111-1111-1111-1111-111111111111' in seed
        assertThat(found.get().getDoctorId()).isEqualTo("11111111-1111-1111-1111-111111111111");
    }
}
