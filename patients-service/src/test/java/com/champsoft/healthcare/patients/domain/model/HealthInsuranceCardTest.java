package com.champsoft.healthcare.patients.domain.model;

import com.champsoft.healthcare.patients.domain.exception.ExpiredHealthInsuranceCardException;
import com.champsoft.healthcare.patients.domain.exception.InvalidInsuranceCardNumber;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class HealthInsuranceCardTest {

    private static final LocalDate FUTURE_EXPIRY = LocalDate.now().plusYears(2);

    @Test
    void shouldCreateHealthInsuranceCardSuccessfully() {

        // ------------------- Act -------------------
        // Raw input without spaces: 4 uppercase letters + 8 digits.
        HealthInsuranceCard card = new HealthInsuranceCard("ABCD12345678", FUTURE_EXPIRY);

        // ------------------- Assert -------------------
        // Business rule: card is stored formatted with spaces: "ABCD 1234 5678".
        assertThat(card.insuranceCardNumber()).isEqualTo("ABCD 1234 5678");
        assertThat(card.getExpiryDate()).isEqualTo(FUTURE_EXPIRY);
    }

    @Test
    void shouldAcceptCardWithExtraSpacesAndLowercase() {

        // ------------------- Act -------------------
        // Business rule: spaces and lowercase are stripped/uppercased before validation.
        HealthInsuranceCard card = new HealthInsuranceCard("abcd 1234 5678", FUTURE_EXPIRY);

        // ------------------- Assert -------------------
        assertThat(card.insuranceCardNumber()).isEqualTo("ABCD 1234 5678");
    }

    @Test
    void shouldThrowWhenCardIsNull() {

        // ------------------- Act + Assert -------------------
        // Business rule: null card is rejected.
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard(null, FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenCardIsBlank() {

        // ------------------- Act + Assert -------------------
        // Business rule: blank card is rejected.
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("   ", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenCardFormatIsInvalid() {

        // ------------------- Act + Assert -------------------
        // Business rule: must match ^[A-Z]{4}\d{8}$ (4 letters + 8 digits).
        // "INVALID123" is too short and has wrong structure.
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("INVALID123", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenCardHasDigitsInLetterSection() {

        // ------------------- Act + Assert -------------------
        // Business rule: first 4 characters must be uppercase letters, not digits.
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("1234ABCD5678", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenCardHasLettersInDigitSection() {

        // ------------------- Act + Assert -------------------
        // Business rule: last 8 characters must be digits, not letters.
        assertThrows(InvalidInsuranceCardNumber.class,
                () -> new HealthInsuranceCard("ABCDABCDABCD", FUTURE_EXPIRY));
    }

    @Test
    void shouldThrowWhenCardIsExpired() {

        // ------------------- Act + Assert -------------------
        // Business rule: expiry date must be in the future.
        assertThrows(ExpiredHealthInsuranceCardException.class,
                () -> new HealthInsuranceCard("ABCD12345678", LocalDate.now().minusDays(1)));
    }

    @Test
    void shouldThrowWhenExpiryDateIsNull() {

        // ------------------- Act + Assert -------------------
        // Business rule: expiry date cannot be null.
        assertThrows(ExpiredHealthInsuranceCardException.class,
                () -> new HealthInsuranceCard("ABCD12345678", null));
    }
}
