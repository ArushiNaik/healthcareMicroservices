package com.champsoft.healthcare.billings.application.service;

import com.champsoft.healthcare.billings.application.exception.BillingNotFoundException;
import com.champsoft.healthcare.billings.application.port.out.BillingRepositoryPort;
import com.champsoft.healthcare.billings.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// NOTE: BillingId has no equals/hashCode, so we use any(BillingId.class)
// instead of BillingId.of("x") in all when() stubs.
@ExtendWith(MockitoExtension.class)
class BillingCrudServiceTest {

    @Mock
    private BillingRepositoryPort repo;

    @InjectMocks
    private BillingCrudService service;

    private static final LocalDate FUTURE_DUE = LocalDate.now().plusDays(30);

    private Billing sampleBilling() {
        return new Billing(
                BillingId.of("billing-1"),
                new InvoiceItem("Consultation", 150.00),
                new DueDate(FUTURE_DUE),
                PaymentMethod.CASH,
                BillingStatus.PENDING
        );
    }

    @Nested
    @DisplayName("Create billing")
    class CreateBillingTests {

        @Test
        void shouldCreateBillingSuccessfully() {
            when(repo.save(any(Billing.class))).thenAnswer(inv -> inv.getArgument(0));

            Billing saved = service.create("Consultation", 150.00,
                    new DueDate(FUTURE_DUE), PaymentMethod.CASH);

            assertThat(saved).isNotNull();
            assertThat(saved.status()).isEqualTo(BillingStatus.PENDING);
            assertThat(saved.invoice().description()).isEqualTo("Consultation");
            assertThat(saved.invoice().getAmountItem()).isEqualTo(150.00);
            assertThat(saved.paymentMethod()).isEqualTo(PaymentMethod.CASH);
            verify(repo).save(any(Billing.class));
        }
    }

    @Nested
    @DisplayName("Read billing")
    class ReadBillingTests {

        @Test
        void shouldReturnBillingWhenFoundById() {
            // BillingId has no equals/hashCode → use any(BillingId.class)
            Billing billing = sampleBilling();
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.of(billing));

            Billing found = service.getById("billing-1");

            assertThat(found).isSameAs(billing);
            verify(repo).findById(any(BillingId.class));
        }

        @Test
        void shouldThrowBillingNotFoundExceptionWhenMissing() {
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.empty());

            assertThrows(BillingNotFoundException.class, () -> service.getById("missing"));
        }

        @Test
        void shouldReturnAllBillings() {
            List<Billing> billings = List.of(sampleBilling(), sampleBilling());
            when(repo.findAll()).thenReturn(billings);

            List<Billing> result = service.list();

            assertThat(result).hasSize(2);
            verify(repo).findAll();
        }
    }

    @Nested
    @DisplayName("Update billing item")
    class UpdateBillingTests {

        @Test
        void shouldUpdateBillingItemSuccessfully() {
            Billing billing = sampleBilling();
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.of(billing));
            when(repo.save(any(Billing.class))).thenAnswer(inv -> inv.getArgument(0));

            Billing updated = service.updateBillingItem("billing-1", "Lab Test", 200.00);

            assertThat(updated.invoice().description()).isEqualTo("Lab Test");
            assertThat(updated.invoice().getAmountItem()).isEqualTo(200.00);
            verify(repo).save(billing);
        }

        @Test
        void shouldThrowBillingNotFoundExceptionWhenUpdatingMissingBilling() {
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.empty());

            assertThrows(BillingNotFoundException.class,
                    () -> service.updateBillingItem("missing", "X-Ray", 300.00));

            verify(repo, never()).save(any(Billing.class));
        }
    }

    @Nested
    @DisplayName("Status transitions")
    class StatusTransitionTests {

        @Test
        void shouldMarkBillingAsPaidSuccessfully() {
            Billing billing = sampleBilling();
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.of(billing));
            when(repo.save(any(Billing.class))).thenAnswer(inv -> inv.getArgument(0));

            Billing paid = service.paid("billing-1");

            assertThat(paid.status()).isEqualTo(BillingStatus.PAID);
            verify(repo).save(billing);
        }

        @Test
        void shouldMarkBillingAsRefundedSuccessfully() {
            Billing billing = sampleBilling();
            billing.paid(); // must be PAID before refund
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.of(billing));
            when(repo.save(any(Billing.class))).thenAnswer(inv -> inv.getArgument(0));

            Billing refunded = service.refunded("billing-1");

            assertThat(refunded.status()).isEqualTo(BillingStatus.REFUNDED);
            verify(repo).save(billing);
        }
    }

    @Nested
    @DisplayName("Delete billing")
    class DeleteBillingTests {

        @Test
        void shouldDeleteBillingSuccessfully() {
            Billing billing = sampleBilling();
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.of(billing));

            service.delete("billing-1");

            verify(repo).findById(any(BillingId.class));
            verify(repo).deleteById(any(BillingId.class));
        }

        @Test
        void shouldThrowBillingNotFoundExceptionWhenDeletingMissingBilling() {
            when(repo.findById(any(BillingId.class))).thenReturn(Optional.empty());

            assertThrows(BillingNotFoundException.class, () -> service.delete("missing"));

            verify(repo, never()).deleteById(any(BillingId.class));
        }
    }
}
