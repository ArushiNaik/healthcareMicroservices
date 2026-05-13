package com.champsoft.healthcare.appointments.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class AppointmentTimeTest {

    @Test
    void shouldCreateAppointmentTimeSuccessfully() {

        // ------------------- Arrange -------------------
        LocalDateTime future = LocalDateTime.now().plusDays(30);

        // ------------------- Act -------------------
        AppointmentTime time = new AppointmentTime(future);

        // ------------------- Assert -------------------
        // Verify the stored value matches what was provided.
        assertThat(time.value()).isEqualTo(future);
    }

    @Test
    void shouldThrowWhenTimeIsInThePast() {

        // ------------------- Arrange -------------------
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        // ------------------- Act + Assert -------------------
        // Business rule: appointment time cannot be in the past.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> new AppointmentTime(past));

        assertThat(ex.getMessage()).isEqualTo("Time cannot be in the past");
    }

    @Test
    void shouldThrowWhenTimeIsJustBeforeNow() {

        // ------------------- Arrange -------------------
        // Just one second in the past — still rejected.
        LocalDateTime justPast = LocalDateTime.now().minusSeconds(1);

        // ------------------- Act + Assert -------------------
        assertThrows(RuntimeException.class,
                () -> new AppointmentTime(justPast));
    }

    @Test
    void shouldCompareAppointmentTimesCorrectly() {

        // ------------------- Arrange -------------------
        LocalDateTime future = LocalDateTime.now().plusDays(30);

        // Create two AppointmentTime records with the same value.
        AppointmentTime time1 = new AppointmentTime(future);
        AppointmentTime time2 = new AppointmentTime(future);

        // Create another AppointmentTime with a different value.
        AppointmentTime time3 = new AppointmentTime(LocalDateTime.now().plusDays(60));

        // ------------------- Assert -------------------
        // Records implement equals() automatically based on their components.
        assertThat(time1).isEqualTo(time2);

        // AppointmentTime records with different values should not be equal.
        assertThat(time1).isNotEqualTo(time3);

        // Equal records should have the same hashCode.
        assertThat(time1.hashCode()).isEqualTo(time2.hashCode());
    }

    @Test
    void shouldReturnValueViaAccessor() {

        // ------------------- Arrange -------------------
        LocalDateTime future = LocalDateTime.now().plusDays(10);
        AppointmentTime time = new AppointmentTime(future);

        // ------------------- Assert -------------------
        // Record accessor returns the exact value passed in.
        assertThat(time.value()).isEqualTo(future);
        assertThat(time.toString()).contains(future.toString());
    }
}
