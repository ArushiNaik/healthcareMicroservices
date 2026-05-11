package com.champsoft.healthcare.patients.infrastructure.persistance;

import com.champsoft.healthcare.patients.domain.model.HealthInsuranceCard;
import com.champsoft.healthcare.patients.domain.model.Address;
import com.champsoft.healthcare.patients.persistence.PatientJpaEntity;
import com.champsoft.healthcare.patients.persistence.SpringDataPatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest → loads only JPA layer (entities + repositories)
// Starts an in-memory H2 database automatically
@DataJpaTest
@ActiveProfiles("testing")
class PatientRepositoryIntegrationTest {

    // Real repository (NOT mocked) — this is an integration test
    @Autowired
    private SpringDataPatientRepository repository;

    // Helper → creates a valid PatientJpaEntity for persistence testing.
    // Uses unique phone/email to avoid constraint violations across tests.
    private PatientJpaEntity buildEntity(String id, String phone, String email, String formattedCard) {
        PatientJpaEntity e = new PatientJpaEntity();
        e.id = id;
        e.firstName = "Alice";
        e.lastName = "Brown";
        e.phoneNumber = phone;
        e.email = email;
        e.dateOfBirth = LocalDate.of(1990, 1, 15);
        // HealthInsuranceCard is @Embedded directly in PatientJpaEntity
        e.insuranceCard = new HealthInsuranceCard(formattedCard, LocalDate.now().plusYears(2));
        e.address = new Address(123, "Main St", "Montreal", "H1A1A1", "Canada");
        e.status = "STABLE";
        return e;
    }

    @Test
    @DisplayName("Should save a patient successfully")
    void shouldSavePatientSuccessfully() {

        // ------------------- Arrange -------------------
        PatientJpaEntity entity = buildEntity("repo-1", "514-100-0001", "repo1@test.com", "ABCD10000001");

        // ------------------- Act -------------------
        PatientJpaEntity saved = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(saved).isNotNull();
        assertThat(saved.id).isEqualTo("repo-1");
        assertThat(saved.firstName).isEqualTo("Alice");
        assertThat(saved.status).isEqualTo("STABLE");
    }

    @Test
    @DisplayName("Should find a patient by id")
    void shouldFindPatientById() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity("repo-2", "514-100-0002", "repo2@test.com", "ABCD20000002"));

        // ------------------- Act -------------------
        Optional<PatientJpaEntity> found = repository.findById("repo-2");

        // ------------------- Assert -------------------
        assertThat(found).isPresent();
        assertThat(found.get().id).isEqualTo("repo-2");
        assertThat(found.get().firstName).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should return true when insurance card exists")
    void shouldReturnTrueWhenInsuranceCardExists() {

        // ------------------- Arrange -------------------
        // HealthInsuranceCard formats "ABCD30000003" → "ABCD 3000 0003"
        repository.save(buildEntity("repo-3", "514-100-0003", "repo3@test.com", "ABCD30000003"));

        // ------------------- Act -------------------
        // The DB stores the formatted card "ABCD 3000 0003"
        boolean exists = repository.existsByInsuranceCardHealthCardNum("ABCD 3000 0003");

        // ------------------- Assert -------------------
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when insurance card does not exist")
    void shouldReturnFalseWhenInsuranceCardDoesNotExist() {

        // ------------------- Act -------------------
        boolean exists = repository.existsByInsuranceCardHealthCardNum("XXXX 9999 9999");

        // ------------------- Assert -------------------
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should delete a patient successfully")
    void shouldDeletePatientSuccessfully() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity("repo-4", "514-100-0004", "repo4@test.com", "ABCD40000004"));

        // ------------------- Act -------------------
        repository.deleteById("repo-4");

        // ------------------- Assert -------------------
        assertThat(repository.findById("repo-4")).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when patient id is not found")
    void shouldReturnEmptyWhenPatientIdIsNotFound() {

        // ------------------- Act -------------------
        Optional<PatientJpaEntity> found = repository.findById("UNKNOWN-ID");

        // ------------------- Assert -------------------
        assertThat(found).isEmpty();
    }
}
