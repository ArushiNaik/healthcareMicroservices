package com.champsoft.healthcare.billings.domain.model;

import com.champsoft.healthcare.billings.domain.exception.InvalidStatusRefund;
import lombok.Setter;

public class Billing {

    private final BillingId id;
    private final DueDate dueDate;
    @Setter
    private PaymentMethod paymentMethod;
    @Setter
    private BillingStatus status;
    public InvoiceItem invoice;

    public Billing(BillingId id,InvoiceItem invoice, DueDate dueDate, PaymentMethod paymentMethod, BillingStatus status) {
        this.id = id;
        this.dueDate=dueDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.invoice = invoice ;
        this.status=status;

    }

    public BillingId id() {
        return id;
    }
    public String idValue() {
        return id.value();
    }

    public DueDate dueDate() {
        return dueDate;
    }

    public Double amount(double amount) {
        return amount;
    }

    public InvoiceItem invoice() {
        return invoice;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public BillingStatus status() {
        return status;
    }

    public void updateBilling(InvoiceItem item){
        this.invoice = item;
    }


    public void paid(){
        this.status = BillingStatus.PAID;
    }


    public void pending(){
        this.status=BillingStatus.PENDING;
    }

    public void refunded(){
        if(this.status==BillingStatus.REFUNDED)throw new RuntimeException("Billing already refunded");
        if(this.status==BillingStatus.PENDING)throw new InvalidStatusRefund("Billing cannot be REFUNDED if billing status is PENDING");
        this.status=BillingStatus.REFUNDED;
    }

    public boolean isEligibleForRefund(){
        return status==BillingStatus.PAID;
    }


}
