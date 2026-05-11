package com.champsoft.healthcare.patients.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class PatientIdTest {

    @Test
    void shouldCreatePatientIdFromValue() {

        // ------------------- Act -------------------
        // Create a PatientId value object from a fixed string value.
        PatientId id = PatientId.of("patient-1");

        // ------------------- Assert -------------------
        // PatientId uses getId() (not value())
        assertThat(id.getId()).isEqualTo("patient-1");

        // toString() should also return the ID value.
        assertThat(id.toString()).isEqualTo("patient-1");
    }

    @Test
    void shouldCreateNewPatientId() {

        // ------------------- Act -------------------
        PatientId id = PatientId.newId();

        // ------------------- Assert -------------------
        assertThat(id).isNotNull();
        assertThat(id.getId()).isNotBlank();
    }

    @Test
    void shouldComparePatientIdsCorrectly() {

        // ------------------- Arrange -------------------
        PatientId id1 = PatientId.of("patient-1");
        PatientId id2 = PatientId.of("patient-1");
        PatientId id3 = PatientId.of("patient-2");

        // ------------------- Assert -------------------
        // Two PatientId objects with the same value should be equal.
        assertThat(id1).isEqualTo(id2);

        // PatientId objects with different values should not be equal.
        assertThat(id1).isNotEqualTo(id3);

        // Equal objects should have the same hashCode.
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
