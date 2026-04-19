package com.champsoft.healthcare.billings.domain.model;

import com.champsoft.healthcare.billings.domain.exception.InvalidInvoiceItemException;

public class InvoiceItem {

    private String description;
    private double amount;

    public InvoiceItem(String description, double amount) {
        if(description.isEmpty()) throw new InvalidInvoiceItemException("Description is required");
        if(amount <=0) throw new InvalidInvoiceItemException("Amount cannot be negative");
        this.description = description;
        this.amount=amount;
    }


    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmountItem() {
        return amount;
    }

    public void setAmountItem(double amount) {
        this.amount = amount;
    }




}
