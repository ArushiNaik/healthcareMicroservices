package com.champsoft.healthcare.doctors.infrastructure.persistance;

import com.champsoft.healthcare.doctors.persistence.DoctorJpaEntity;
import com.champsoft.healthcare.doctors.persistence.DoctorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class DoctorRepositoryIntegrationTest {

    @Autowired
    private DoctorRepository repository;

    private DoctorJpaEntity buildEntity(String id) {
        DoctorJpaEntity e = new DoctorJpaEntity();
        e.setId(id);
        e.setFirstName("Alice");
        e.setLastName("Brown");
        e.setSpecialty("Cardiology");
        e.setLicenseExpiryDate(LocalDate.now().plusYears(2));
        e.setActive(true);
        return e;
    }

    @Test
    @DisplayName("Should save a doctor successfully")
    void shouldSaveDoctorSuccessfully() {
        String id = UUID.randomUUID().toString();
        DoctorJpaEntity saved = repository.save(buildEntity(id));

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getFirstName()).isEqualTo("Alice");
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should find a doctor by ID")
    void shouldFindDoctorById() {
        String id = UUID.randomUUID().toString();
        repository.save(buildEntity(id));

        Optional<DoctorJpaEntity> found = repository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(id);
        assertThat(found.get().getSpecialty()).isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Should return empty when doctor ID not found")
    void shouldReturnEmptyWhenDoctorIdNotFound() {
        assertThat(repository.findById("UNKNOWN-ID")).isEmpty();
    }

    @Test
    @DisplayName("Should return true when doctor ID exists")
    void shouldReturnTrueWhenDoctorIdExists() {
        String id = UUID.randomUUID().toString();
        repository.save(buildEntity(id));
        assertThat(repository.existsById(id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when doctor ID does not exist")
    void shouldReturnFalseWhenDoctorIdDoesNotExist() {
        assertThat(repository.existsById("UNKNOWN-ID")).isFalse();
    }

    @Test
    @DisplayName("Should find all doctors")
    void shouldFindAllDoctors() {
        repository.save(buildEntity(UUID.randomUUID().toString()));
        repository.save(buildEntity(UUID.randomUUID().toString()));
        assertThat(repository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should delete a doctor successfully")
    void shouldDeleteDoctorSuccessfully() {
        String id = UUID.randomUUID().toString();
        repository.save(buildEntity(id));
        repository.deleteById(id);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should update doctor active status")
    void shouldUpdateDoctorActiveStatus() {
        String id = UUID.randomUUID().toString();
        DoctorJpaEntity entity = buildEntity(id);
        repository.save(entity);

        entity.setActive(false);
        DoctorJpaEntity updated = repository.save(entity);

        assertThat(updated.isActive()).isFalse();
    }
}