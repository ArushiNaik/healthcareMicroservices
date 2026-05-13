package com.champsoft.healthcare.patients.domain.model;

import com.champsoft.healthcare.patients.domain.exception.InvalidAddressException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class PatientAddressTest {

    @Test
    void shouldCreateAddressSuccessfully() {

        // ------------------- Act -------------------
        Address address = new Address(123, "Main Street", "Montreal", "H1A1A1", "Canada");

        // ------------------- Assert -------------------
        assertThat(address.getStreetNumber()).isEqualTo(123);
        assertThat(address.getStreetName()).isEqualTo("Main Street");
        assertThat(address.getCity()).isEqualTo("Montreal");
        assertThat(address.getPostalCode()).isEqualTo("H1A1A1");
        assertThat(address.getCountry()).isEqualTo("Canada");
    }

    @Test
    void shouldThrowWhenStreetNumberIsNull() {

        // ------------------- Act + Assert -------------------
        // Business rule: street number is mandatory.
        assertThrows(InvalidAddressException.class,
                () -> new Address(null, "Main Street", "Montreal", "H1A1A1", "Canada"));
    }

    @Test
    void shouldThrowWhenStreetNameIsEmpty() {

        // ------------------- Act + Assert -------------------
        // Business rule: street name cannot be empty.
        assertThrows(InvalidAddressException.class,
                () -> new Address(123, "", "Montreal", "H1A1A1", "Canada"));
    }

    @Test
    void shouldThrowWhenCityIsEmpty() {

        // ------------------- Act + Assert -------------------
        // Business rule: city cannot be empty.
        assertThrows(InvalidAddressException.class,
                () -> new Address(123, "Main Street", "", "H1A1A1", "Canada"));
    }

    @Test
    void shouldThrowWhenPostalCodeIsEmpty() {

        // ------------------- Act + Assert -------------------
        // Business rule: postal code cannot be empty.
        assertThrows(InvalidAddressException.class,
                () -> new Address(123, "Main Street", "Montreal", "", "Canada"));
    }

    @Test
    void shouldThrowWhenCountryIsEmpty() {

        // ------------------- Act + Assert -------------------
        // Business rule: country cannot be empty.
        assertThrows(InvalidAddressException.class,
                () -> new Address(123, "Main Street", "Montreal", "H1A1A1", ""));
    }
}
