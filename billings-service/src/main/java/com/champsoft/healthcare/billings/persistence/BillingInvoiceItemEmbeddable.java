package com.champsoft.healthcare.billings.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BillingInvoiceItemEmbeddable {

    @Column(name="description_item", nullable = false)
    public String description;

    @Column(name="amount_item", nullable = false)
    public double amount;

    protected BillingInvoiceItemEmbeddable() {
    }

    public BillingInvoiceItemEmbeddable(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }
}
