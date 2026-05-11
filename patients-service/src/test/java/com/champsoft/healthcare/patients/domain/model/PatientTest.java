package com.champsoft.healthcare.patients.domain.model;

import com.champsoft.healthcare.patients.domain.exception.ExpiredHealthInsuranceCardException;
import com.champsoft.healthcare.patients.domain.exception.InvalidAddressException;
import com.champsoft.healthcare.patients.domain.exception.InvalidInsuranceCardNumber;
import com.champsoft.healthcare.patients.domain.exception.PatientStatusException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class PatientTest {

    private static final LocalDate FUTURE_EXPIRY = LocalDate.now().plusYears(2);
    private static final LocalDate ADULT_DOB     = LocalDate.now().minusYears(30);

    // Helper → creates a valid HealthInsuranceCard (raw format: 4 letters + 8 digits)
    private HealthInsuranceCard card(String raw) {
        return new HealthInsuranceCard(raw, FUTURE_EXPIRY);
    }

    // Helper → creates a valid Address
    private Address address() {
        return new Address(123, "Main Street", "Montreal", "H1A1A1", "Canada");
    }

    // Helper → creates a valid Patient
    private Patient patient(String id) {
        return new Patient(
                PatientId.of(id),
                "Alice", "Brown",
                "514-555-0001", "alice@example.com",
                ADULT_DOB,
                card("ABCD12345678"),
                address(),
                PatientStatus.STABLE
        );
    }

    @Test
    void shouldCreatePatientSuccessfully() {

        // ------------------- Act -------------------
        Patient p = patient("patient-1");

        // ------------------- Assert -------------------
        assertThat(p.id().getId()).isEqualTo("patient-1");
        assertThat(p.firstName()).isEqualTo("Alice");
        assertThat(p.lastName()).isEqualTo("Brown");
        assertThat(p.status()).isEqualTo(PatientStatus.STABLE);

        // Business rule: fullName combines first + last.
        assertThat(p.fullName()).isEqualTo("Alice Brown");
    }

    @Test
    void shouldChangeStatusSuccessfully() {

        // ------------------- Arrange -------------------
        Patient p = patient("patient-1");

        // ------------------- Act -------------------
        p.changeStatus(PatientStatus.CRITICAL);

        // ------------------- Assert -------------------
        assertThat(p.status()).isEqualTo(PatientStatus.CRITICAL);
    }

    @Test
    void shouldThrowPatientStatusExceptionWhenChangingToSameStatus() {

        // ------------------- Arrange -------------------
        Patient p = patient("patient-1");
        assertThat(p.status()).isEqualTo(PatientStatus.STABLE);

        // ------------------- Act + Assert -------------------
        // Business rule: cannot change to the same status.
        assertThrows(PatientStatusException.class,
                () -> p.changeStatus(PatientStatus.STABLE));
    }

    @Test
    void shouldReturnEligibleForAdultPatient() {

        // ------------------- Arrange -------------------
        // Adult patient (30 years old)
        Patient p = patient("patient-1");

        // ------------------- Assert -------------------
        assertThat(p.isEligibleForAppointment()).isTrue();
    }

    @Test
    void shouldReturnNotEligibleForMinorPatient() {

        // ------------------- Arrange -------------------
        // Minor patient (15 years old)
        Patient minor = new Patient(
                PatientId.of("minor"),
                "Bob", "Young",
                "514-555-0002", "bob@example.com",
                LocalDate.now().minusYears(15),
                card("WXYZ12345678"),
                address(),
                PatientStatus.STABLE
        );

        // ------------------- Assert -------------------
        assertThat(minor.isEligibleForAppointment()).isFalse();
    }

    @Test
    void shouldUpdateAddressSuccessfully() {

        // ------------------- Arrange -------------------
        Patient p = patient("patient-1");

        // ------------------- Act -------------------
        p.updateAddress(new Address(456, "Elm Street", "Quebec City", "G1A1A1", "Canada"));

        // ------------------- Assert -------------------
        assertThat(p.getAddress().getStreetNumber()).isEqualTo(456);
        assertThat(p.getAddress().getCity()).isEqualTo("Quebec City");
    }

    @Test
    void shouldUpdateInsuranceCardSuccessfully() {

        // ------------------- Arrange -------------------
        Patient p = patient("patient-1");

        // ------------------- Act -------------------
        p.updateInsuranceCard(new HealthInsuranceCard("WXYZ87654321", FUTURE_EXPIRY));

        // ------------------- Assert -------------------
        // Business rule: card is stored formatted with spaces.
        assertThat(p.insuranceCard().insuranceCardNumber()).isEqualTo("WXYZ 8765 4321");
    }

    // ---- HealthInsuranceCard validation ----

    @Test
    void shouldFormatInsuranceCardWithSpaces() {

        // ------------------- Act -------------------
        HealthInsuranceCard c = new HealthInsuranceCard("ABCD12345678", FUTURE_EXPIRY);

        // ------------------- Assert -------------------
        assertThat(c.insuranceCardNumber()).isEqualTo("ABCD 1234 5678");
    }

    @Test
    void shouldThrowWhenInsuranceCardIsNull() {
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard(null, FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenInsuranceCardIsBlank() {
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("   ", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenInsuranceCardFormatIsInvalid() {
        // Business rule: must match ^[A-Z]{4}\d{8}$
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("INVALID-FORMAT", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenInsuranceCardIsExpired() {
        assertThrows(ExpiredHealthInsuranceCardException.class,
                () -> new HealthInsuranceCard("ABCD12345678", LocalDate.now().minusDays(1)));
    }

    // ---- Address validation ----

    @Test
    void shouldThrowWhenAddressStreetNumberIsNull() {
        assertThrows(InvalidAddressException.class,
                () -> new Address(null, "Main St", "Montreal", "H1A1A1", "Canada"));
    }

    @Test
    void shouldThrowWhenAddressStreetNameIsEmpty() {
        assertThrows(InvalidAddressException.class,
                () -> new Address(1, "", "Montreal", "H1A1A1", "Canada"));
    }
}
