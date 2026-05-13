package com.champsoft.healthcare.doctors.domain.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class DoctorAvailabilityTest {

    @Test
    void shouldCreateDoctorAvailabilitySuccessfully() {

        // ------------------- Act -------------------
        // Create a valid availability slot: Monday 9:00 → 17:00.
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // ------------------- Assert -------------------
        assertThat(availability.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(availability.startTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(availability.endTime()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void shouldThrowWhenStartTimeIsAfterEndTime() {

        // ------------------- Act + Assert -------------------
        // Business rule: start time must be before end time.
        assertThrows(IllegalArgumentException.class,
                () -> new DoctorAvailability(
                        DayOfWeek.MONDAY,
                        LocalTime.of(17, 0),
                        LocalTime.of(9, 0)
                ));
    }

    @Test
    void shouldCreateDoctorAvailabilityWhenStartTimeEqualsEndTime() {

        // ------------------- Act + Assert -------------------
        // The guard is startTime.isAfter(endTime) — equal times are NOT rejected.
        // A zero-length slot is allowed by the domain rule as written.
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 0)
        );

        assertThat(availability.startTime()).isEqualTo(availability.endTime());
    }

    @Test
    void shouldMatchWhenDateTimeIsWithinSlot() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // A Monday at 10:30 — inside the slot.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 10, 30); // 2025-01-06 is a Monday

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isTrue();
    }

    @Test
    void shouldMatchWhenDateTimeIsExactlyAtStartTime() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // Exactly at start time → inclusive boundary.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 9, 0);

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isTrue();
    }

    @Test
    void shouldMatchWhenDateTimeIsExactlyAtEndTime() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // Exactly at end time → inclusive boundary.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 17, 0);

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isTrue();
    }

    @Test
    void shouldNotMatchWhenDateTimeIsBeforeStartTime() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // Monday at 8:59 — before the slot starts.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 8, 59);

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isFalse();
    }

    @Test
    void shouldNotMatchWhenDateTimeIsAfterEndTime() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // Monday at 17:01 — after the slot ends.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 17, 1);

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isFalse();
    }

    @Test
    void shouldNotMatchWhenDayOfWeekIsDifferent() {

        // ------------------- Arrange -------------------
        DoctorAvailability availability = new DoctorAvailability(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );

        // Same time but on a Tuesday — wrong day.
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 7, 10, 30); // 2025-01-07 is a Tuesday

        // ------------------- Act + Assert -------------------
        assertThat(availability.matches(dateTime)).isFalse();
    }

    @Test
    void shouldCompareAvailabilitiesCorrectly() {

        // ------------------- Arrange -------------------
        DoctorAvailability av1 = new DoctorAvailability(
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        DoctorAvailability av2 = new DoctorAvailability(
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        DoctorAvailability av3 = new DoctorAvailability(
                DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

        // ------------------- Assert -------------------
        // Records implement equals() automatically based on their components.
        assertThat(av1).isEqualTo(av2);
        assertThat(av1).isNotEqualTo(av3);
        assertThat(av1.hashCode()).isEqualTo(av2.hashCode());
    }
}
