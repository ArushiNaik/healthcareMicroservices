package com.champsoft.healthcare.billings.persistence;

import com.champsoft.healthcare.billings.domain.model.*;

public class BillingMapper {

    // ENTITY → DOMAIN
    public static Billing toDomain(BillingJpaEntity e) {

        InvoiceItem item = new InvoiceItem(e.getDescription(), e.getAmount());

        return new Billing(
                new BillingId(e.getId()),
                item,
                new DueDate(e.getDueDate()),
                e.getPaymentMethod(),
                e.getStatus()
        );
    }

    // DOMAIN → ENTITY
    public static BillingJpaEntity toEntity(Billing b) {

        BillingJpaEntity e = new BillingJpaEntity();

        e.setId(b.idValue()); // 🔥 ONLY STRING HERE
        e.setDueDate(b.dueDate().dueDate());
        e.setPaymentMethod(b.paymentMethod());
        e.setStatus(b.status());

        e.setDescription(b.invoice().description());
        e.setAmount(b.invoice().getAmountItem());

        return e;
    }
}