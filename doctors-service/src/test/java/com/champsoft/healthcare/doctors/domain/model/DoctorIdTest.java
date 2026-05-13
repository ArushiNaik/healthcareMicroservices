package com.champsoft.healthcare.doctors.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class DoctorIdTest {

    @Test
    void shouldCreateDoctorIdFromValue() {

        // ------------------- Arrange -------------------
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // ------------------- Act -------------------
        // Create a DoctorId value object from a known UUID.
        DoctorId id = new DoctorId(uuid);

        // ------------------- Assert -------------------
        // Verify that the DoctorId stores the expected UUID value.
        assertThat(id.value()).isEqualTo(uuid);

        // toString() on a record includes the field, so it should contain the UUID string.
        assertThat(id.toString()).contains(uuid.toString());
    }

    @Test
    void shouldCreateNewDoctorId() {

        // ------------------- Act -------------------
        // Create a new DoctorId with a randomly generated UUID.
        DoctorId id = DoctorId.newId();

        // ------------------- Assert -------------------
        // The generated DoctorId should not be null.
        assertThat(id).isNotNull();

        // The UUID value inside should not be null.
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldRejectNullValue() {

        // ------------------- Assert -------------------
        // Passing null should throw an IllegalArgumentException,
        // because a DoctorId with no value is meaningless.
        assertThatThrownBy(() -> new DoctorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("DoctorId cannot be null");
    }

    @Test
    void shouldCompareDoctorIdsCorrectly() {

        // ------------------- Arrange -------------------
        UUID uuid = UUID.randomUUID();

        // Create two DoctorId objects with the same UUID value.
        DoctorId id1 = new DoctorId(uuid);
        DoctorId id2 = new DoctorId(uuid);

        // Create another DoctorId with a different UUID.
        DoctorId id3 = DoctorId.newId();

        // ------------------- Assert -------------------
        // Two DoctorId records with the same UUID should be equal.
        assertThat(id1).isEqualTo(id2);

        // DoctorId records with different UUIDs should not be equal.
        assertThat(id1).isNotEqualTo(id3);

        // Equal records should have the same hashCode.
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}

