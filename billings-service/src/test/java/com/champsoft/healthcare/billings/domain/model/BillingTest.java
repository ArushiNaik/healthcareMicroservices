package com.champsoft.healthcare.billings.domain.model;

import com.champsoft.healthcare.billings.application.exception.InvalidPriceException;
import com.champsoft.healthcare.billings.domain.exception.InvalidInvoiceItemException;
import com.champsoft.healthcare.billings.domain.exception.InvalidPaymentMethodException;
import com.champsoft.healthcare.billings.domain.exception.InvalidStatusRefund;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database

import com.champsoft.healthcare.billings.application.exception.InvalidPriceException;
import com.champsoft.healthcare.billings.domain.exception.InvalidInvoiceItemException;
import com.champsoft.healthcare.billings.domain.exception.InvalidPaymentMethodException;
import com.champsoft.healthcare.billings.domain.exception.InvalidStatusRefund;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillingTest {

    private static final LocalDate FUTURE_DUE = LocalDate.now().plusDays(30);

    private Billing validBilling(String id) {
        return new Billing(
                BillingId.of(id),
                new InvoiceItem("Consultation", 150.00),
                new DueDate(FUTURE_DUE),
                PaymentMethod.CASH,
                BillingStatus.PENDING
        );
    }

    @Test
    void shouldCreateBillingWithPendingStatus() {
        Billing billing = validBilling("billing-1");
        assertThat(billing.id().value()).isEqualTo("billing-1");
        assertThat(billing.status()).isEqualTo(BillingStatus.PENDING);
        assertThat(billing.paymentMethod()).isEqualTo(PaymentMethod.CASH);
        assertThat(billing.invoice().description()).isEqualTo("Consultation");
        assertThat(billing.invoice().getAmountItem()).isEqualTo(150.00);
    }

    @Test
    void shouldTransitionFromPendingToPaid() {
        Billing billing = validBilling("billing-1");
        billing.paid();
        assertThat(billing.status()).isEqualTo(BillingStatus.PAID);
    }

    @Test
    void shouldTransitionFromPaidToPending() {
        Billing billing = validBilling("billing-1");
        billing.paid();
        billing.pending();
        assertThat(billing.status()).isEqualTo(BillingStatus.PENDING);
    }

    @Test
    void shouldRefundPaidBillingSuccessfully() {
        Billing billing = validBilling("billing-1");
        billing.paid();
        billing.refunded();
        assertThat(billing.status()).isEqualTo(BillingStatus.REFUNDED);
    }

    @Test
    void shouldThrowInvalidStatusRefundWhenRefundingPendingBilling() {
        // PENDING → refunded() throws InvalidStatusRefund
        Billing billing = validBilling("billing-1");
        assertThat(billing.status()).isEqualTo(BillingStatus.PENDING);
        assertThrows(InvalidStatusRefund.class, billing::refunded);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRefundingAlreadyRefundedBilling() {
        // REFUNDED → refunded() again throws plain RuntimeException (not InvalidStatusRefund)
        Billing billing = validBilling("billing-1");
        billing.paid();
        billing.refunded();
        assertThat(billing.status()).isEqualTo(BillingStatus.REFUNDED);
        // The Billing.refunded() source: if REFUNDED → throw new RuntimeException(...)
        assertThrows(RuntimeException.class, billing::refunded);
    }

    @Test
    void shouldReturnTrueForEligibleRefundWhenPaid() {
        Billing billing = validBilling("billing-1");
        billing.paid();
        assertThat(billing.isEligibleForRefund()).isTrue();
    }

    @Test
    void shouldReturnFalseForEligibleRefundWhenPending() {
        Billing billing = validBilling("billing-1");
        assertThat(billing.isEligibleForRefund()).isFalse();
    }

    @Test
    void shouldUpdateBillingItemSuccessfully() {
        Billing billing = validBilling("billing-1");
        billing.updateBilling(new InvoiceItem("Lab Test", 200.00));
        assertThat(billing.invoice().description()).isEqualTo("Lab Test");
        assertThat(billing.invoice().getAmountItem()).isEqualTo(200.00);
    }

    // ---- InvoiceItem validation ----

    @Test
    void shouldThrowInvalidInvoiceItemExceptionWhenDescriptionIsEmpty() {
        assertThrows(InvalidInvoiceItemException.class, () -> new InvoiceItem("", 100.00));
    }

    @Test
    void shouldThrowInvalidPriceExceptionWhenAmountIsZero() {
        assertThrows(InvalidPriceException.class, () -> new InvoiceItem("Test", 0.0));
    }

    @Test
    void shouldThrowInvalidPriceExceptionWhenAmountIsNegative() {
        assertThrows(InvalidPriceException.class, () -> new InvoiceItem("Test", -50.0));
    }

    // ---- DueDate validation ----

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDueDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DueDate(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDueDateIsInThePast() {
        assertThrows(IllegalArgumentException.class,
                () -> new DueDate(LocalDate.now().minusDays(1)));
    }

    // ---- BillingId tests ----

    @Test
    void shouldCreateBillingIdFromValue() {
        BillingId id = BillingId.of("billing-abc");
        assertThat(id.value()).isEqualTo("billing-abc");
    }

    @Test
    void shouldGenerateNewBillingId() {
        BillingId id = BillingId.newId();
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotBlank();
    }

    // ---- PaymentMethod validation ----

    @Test
    void shouldThrowInvalidPaymentMethodExceptionForUnknownMethod() {
        assertThrows(InvalidPaymentMethodException.class, () -> PaymentMethod.from("BITCOIN"));
    }

    @Test
    void shouldResolvePaymentMethodCashFromString() {
        assertThat(PaymentMethod.from("CASH")).isEqualTo(PaymentMethod.CASH);
    }

    @Test
    void shouldResolvePaymentMethodCaseInsensitively() {
        assertThat(PaymentMethod.from("credit_card")).isEqualTo(PaymentMethod.CREDIT_CARD);
    }
}
