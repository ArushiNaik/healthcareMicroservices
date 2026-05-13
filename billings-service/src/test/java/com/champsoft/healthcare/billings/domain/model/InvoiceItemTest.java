package com.champsoft.healthcare.billings.domain.model;

import com.champsoft.healthcare.billings.application.exception.InvalidPriceException;
import com.champsoft.healthcare.billings.domain.exception.InvalidInvoiceItemException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class InvoiceItemTest {

    @Test
    void shouldCreateInvoiceItemSuccessfully() {

        // ------------------- Act -------------------
        InvoiceItem item = new InvoiceItem("Consultation", 150.00);

        // ------------------- Assert -------------------
        assertThat(item.description()).isEqualTo("Consultation");
        assertThat(item.getAmountItem()).isEqualTo(150.00);
    }

    @Test
    void shouldUpdateDescriptionSuccessfully() {

        // ------------------- Arrange -------------------
        InvoiceItem item = new InvoiceItem("Consultation", 150.00);

        // ------------------- Act -------------------
        item.setDescription("Lab Test");

        // ------------------- Assert -------------------
        assertThat(item.description()).isEqualTo("Lab Test");
    }

    @Test
    void shouldUpdateAmountSuccessfully() {

        // ------------------- Arrange -------------------
        InvoiceItem item = new InvoiceItem("Consultation", 150.00);

        // ------------------- Act -------------------
        item.setAmountItem(200.00);

        // ------------------- Assert -------------------
        assertThat(item.getAmountItem()).isEqualTo(200.00);
    }

    @Test
    void shouldThrowWhenDescriptionIsEmpty() {

        // ------------------- Act + Assert -------------------
        // Business rule: description cannot be empty.
        assertThrows(InvalidInvoiceItemException.class,
                () -> new InvoiceItem("", 100.00));
    }

    @Test
    void shouldThrowWhenAmountIsZero() {

        // ------------------- Act + Assert -------------------
        // Business rule: amount must be strictly positive (> 0).
        assertThrows(InvalidPriceException.class,
                () -> new InvoiceItem("Consultation", 0.0));
    }

    @Test
    void shouldThrowWhenAmountIsNegative() {

        // ------------------- Act + Assert -------------------
        // Business rule: negative amount is not a valid price.
        assertThrows(InvalidPriceException.class,
                () -> new InvoiceItem("Consultation", -50.0));
    }
}
