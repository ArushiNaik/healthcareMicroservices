package com.champsoft.healthcare.appointments.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class AppointmentIdTest {

    @Test
    void shouldCreateAppointmentIdFromValue() {

        // ------------------- Act -------------------
        // Create an AppointmentId record from a fixed string value.
        // Records automatically store the value passed into the canonical constructor.
        AppointmentId id = new AppointmentId("appointment-1");

        // ------------------- Assert -------------------
        // Verify that the AppointmentId stores the expected value.
        assertThat(id.value()).isEqualTo("appointment-1");

        // toString() on a record includes the field, so it should contain the value string.
        assertThat(id.toString()).contains("appointment-1");
    }

    @Test
    void shouldCompareAppointmentIdsCorrectly() {

        // ------------------- Arrange -------------------
        // Create two AppointmentId records with the same value.
        AppointmentId id1 = new AppointmentId("appointment-1");
        AppointmentId id2 = new AppointmentId("appointment-1");

        // Create another AppointmentId with a different value.
        AppointmentId id3 = new AppointmentId("appointment-2");

        // ------------------- Assert -------------------
        // Two AppointmentId records with the same value should be equal.
        // Records implement equals() automatically based on their components.
        assertThat(id1).isEqualTo(id2);

        // AppointmentId records with different values should not be equal.
        assertThat(id1).isNotEqualTo(id3);

        // Equal records should have the same hashCode.
        // This is important when records are used in collections such as HashSet or HashMap.
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}

