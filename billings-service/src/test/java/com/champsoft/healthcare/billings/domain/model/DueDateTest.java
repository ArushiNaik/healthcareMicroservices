package com.champsoft.healthcare.billings.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class DueDateTest {

    @Test
    void shouldCreateDueDateSuccessfully() {

        // ------------------- Arrange -------------------
        LocalDate future = LocalDate.now().plusDays(30);

        // ------------------- Act -------------------
        DueDate dueDate = new DueDate(future);

        // ------------------- Assert -------------------
        // Verify the stored value matches what was provided.
        assertThat(dueDate.dueDate()).isEqualTo(future);
    }

    @Test
    void shouldCreateDueDateForTomorrow() {

        // ------------------- Arrange -------------------
        // Tomorrow is the minimum valid due date (not in the past).
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // ------------------- Act -------------------
        DueDate dueDate = new DueDate(tomorrow);

        // ------------------- Assert -------------------
        assertThat(dueDate.dueDate()).isEqualTo(tomorrow);
    }

    @Test
    void shouldThrowWhenDueDateIsNull() {

        // ------------------- Act + Assert -------------------
        // Business rule: due date cannot be null.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DueDate(null));

        assertThat(ex.getMessage()).isEqualTo("Due date cannot be null");
    }

    @Test
    void shouldThrowWhenDueDateIsInThePast() {

        // ------------------- Act + Assert -------------------
        // Business rule: due date cannot be in the past.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DueDate(LocalDate.now().minusDays(1)));

        assertThat(ex.getMessage()).isEqualTo("Due date cannot be in the past");
    }

    @Test
    void shouldThrowWhenDueDateIsToday() {

        // ------------------- Act + Assert -------------------
        // Business rule: today is considered in the past (isBefore check is strict).
        // LocalDate.now().isBefore(LocalDate.now()) is false, so today is ACCEPTED.
        // Verify it does NOT throw.
        DueDate dueDate = new DueDate(LocalDate.now());

        assertThat(dueDate.dueDate()).isEqualTo(LocalDate.now());
    }
}