package com.champsoft.healthcare.billings.infrastructure.persistance;

import com.champsoft.healthcare.billings.domain.model.BillingStatus;
import com.champsoft.healthcare.billings.domain.model.PaymentMethod;
import com.champsoft.healthcare.billings.persistence.BillingJpaEntity;
import com.champsoft.healthcare.billings.persistence.SpringDataBillingRepository;
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

// @DataJpaTest → loads only JPA layer (entities + repositories)
// Starts an in-memory H2 database automatically
@DataJpaTest
@ActiveProfiles("testing")
class BillingRepositoryIntegrationTest {

    // Real repository (NOT mocked)
    @Autowired
    private SpringDataBillingRepository repository;

    // Helper → creates a valid BillingJpaEntity for persistence testing.
    private BillingJpaEntity buildEntity(String id) {
        BillingJpaEntity e = new BillingJpaEntity();
        e.setId(id);
        e.setDescription("Consultation");
        e.setAmount(150.00);
        e.setDueDate(LocalDate.now().plusDays(30));
        e.setPaymentMethod(PaymentMethod.CASH);
        e.setStatus(BillingStatus.PENDING);
        return e;
    }

    @Test
    @DisplayName("Should save a billing successfully")
    void shouldSaveBillingSuccessfully() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        BillingJpaEntity entity = buildEntity(id);

        // ------------------- Act -------------------
        BillingJpaEntity saved = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getDescription()).isEqualTo("Consultation");
        assertThat(saved.getStatus()).isEqualTo(BillingStatus.PENDING);
        assertThat(saved.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
    }

    @Test
    @DisplayName("Should find a billing by ID")
    void shouldFindBillingById() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        repository.save(buildEntity(id));

        // ------------------- Act -------------------
        Optional<BillingJpaEntity> found = repository.findById(id);

        // ------------------- Assert -------------------
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(id);
        assertThat(found.get().getAmount()).isEqualTo(150.00);
    }

    @Test
    @DisplayName("Should return empty when billing ID not found")
    void shouldReturnEmptyWhenBillingIdNotFound() {

        // ------------------- Act -------------------
        Optional<BillingJpaEntity> found = repository.findById("UNKNOWN-ID");

        // ------------------- Assert -------------------
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all billings")
    void shouldFindAllBillings() {

        // ------------------- Arrange -------------------
        repository.save(buildEntity(UUID.randomUUID().toString()));
        repository.save(buildEntity(UUID.randomUUID().toString()));

        // ------------------- Act -------------------
        List<BillingJpaEntity> all = repository.findAll();

        // ------------------- Assert -------------------
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should delete a billing successfully")
    void shouldDeleteBillingSuccessfully() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        repository.save(buildEntity(id));

        // ------------------- Act -------------------
        repository.deleteById(id);

        // ------------------- Assert -------------------
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should update billing status to PAID")
    void shouldUpdateBillingStatusToPaid() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        BillingJpaEntity entity = buildEntity(id);
        repository.save(entity);

        // ------------------- Act -------------------
        entity.setStatus(BillingStatus.PAID);
        BillingJpaEntity updated = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(updated.getStatus()).isEqualTo(BillingStatus.PAID);
    }

    @Test
    @DisplayName("Should update billing status to REFUNDED")
    void shouldUpdateBillingStatusToRefunded() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        BillingJpaEntity entity = buildEntity(id);
        entity.setStatus(BillingStatus.PAID);
        repository.save(entity);

        // ------------------- Act -------------------
        entity.setStatus(BillingStatus.REFUNDED);
        BillingJpaEntity updated = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(updated.getStatus()).isEqualTo(BillingStatus.REFUNDED);
    }

    @Test
    @DisplayName("Should update billing description and amount")
    void shouldUpdateBillingDescriptionAndAmount() {

        // ------------------- Arrange -------------------
        String id = UUID.randomUUID().toString();
        BillingJpaEntity entity = buildEntity(id);
        repository.save(entity);

        // ------------------- Act -------------------
        entity.setDescription("Lab Test");
        entity.setAmount(300.00);
        BillingJpaEntity updated = repository.save(entity);

        // ------------------- Assert -------------------
        assertThat(updated.getDescription()).isEqualTo("Lab Test");
        assertThat(updated.getAmount()).isEqualTo(300.00);
    }
}
