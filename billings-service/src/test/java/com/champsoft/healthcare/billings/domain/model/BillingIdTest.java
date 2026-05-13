package com.champsoft.healthcare.billings.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class BillingIdTest {

    @Test
    void shouldCreateBillingIdFromValue() {

        // ------------------- Act -------------------
        // Create a BillingId value object from a fixed string value.
        // This is useful when we already know the ID, for example in tests or when loading existing data.
        BillingId id = BillingId.of("billing-1");

        // ------------------- Assert -------------------
        // Verify that the BillingId stores the expected value.
        assertThat(id.value()).isEqualTo("billing-1");
    }

    @Test
    void shouldCreateNewBillingId() {

        // ------------------- Act -------------------
        // Create a new BillingId automatically.
        // This method generates a unique ID for a new billing record.
        BillingId id = BillingId.newId();

        // ------------------- Assert -------------------
        // The generated BillingId object should not be null.
        assertThat(id).isNotNull();

        // The generated ID value should not be empty or blank.
        assertThat(id.value()).isNotBlank();
    }

    @Test
    void shouldHaveDifferentValuesForTwoNewIds() {

        // ------------------- Act -------------------
        // Generate two separate BillingIds.
        BillingId id1 = BillingId.newId();
        BillingId id2 = BillingId.newId();

        // ------------------- Assert -------------------
        // Each generated ID should be unique — they must not share the same value.
        assertThat(id1.value()).isNotEqualTo(id2.value());
    }
}

