package com.champsoft.healthcare.appointments.infrastructure.ACL;



import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.billings.application.service.BillingCrudService;
import com.champsoft.healthcare.billings.domain.model.DueDate;
import com.champsoft.healthcare.billings.domain.model.PaymentMethod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BillingAdapter implements BillingPort {

    private final BillingCrudService billingService;

    public BillingAdapter(BillingCrudService billingService) {
        this.billingService = billingService;
    }

    @Override
    public void createBill(String appointmentId, String patientId) {
        // prepare raw arguments for BillingCrudService
        String description = "Billing for appointment " + appointmentId;
        double amount = 100.0;  // or calculate dynamically
        DueDate dueDate = new DueDate(LocalDate.now().plusDays(7));
        PaymentMethod method = PaymentMethod.CREDIT_CARD;

        // call the service with expected argument list
        billingService.create(description, amount, dueDate, method);
    }
}