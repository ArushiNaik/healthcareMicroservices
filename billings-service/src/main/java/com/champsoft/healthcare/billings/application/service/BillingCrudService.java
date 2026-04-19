package com.champsoft.healthcare.billings.application.service;

import com.champsoft.healthcare.billings.application.exception.BillingNotFoundException;
import com.champsoft.healthcare.billings.application.port.out.BillingRepositoryPort;
import com.champsoft.healthcare.billings.domain.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillingCrudService {
    private final BillingRepositoryPort repo;

    public BillingCrudService(@Qualifier("billingRepositoryPort") BillingRepositoryPort repo) {
        this.repo = repo;
    }
    @Transactional
    public Billing create(String description, double amount, DueDate dueDate, PaymentMethod paymentMethod){
            InvoiceItem item = new InvoiceItem(description,amount);
            var bill = new Billing(BillingId.newId(),item,dueDate,paymentMethod,BillingStatus.PENDING);
            return repo.save(bill);
    }

    @Transactional(readOnly = true)
    public Billing getById(String id){
        return repo.findById(BillingId.of(id)).orElseThrow(()-> new BillingNotFoundException("Billing not found: "+ id ));
    }

    @Transactional(readOnly = true)
    public List<Billing> list(){
        return repo.findAll();
    }

    @Transactional
    public Billing refunded(String id){
        var bill = getById(id);
        bill.refunded();
        return repo.save(bill);
    }

    @Transactional
    public Billing paid(String id){
        var bill = getById(id);
        bill.paid();
        return repo.save(bill);
    }

    @Transactional
    public void delete(String id){
        getById(id);
        repo.deleteById(BillingId.of(id));
    }

    @Transactional
    public Billing updateBillingItem(String id,String description,double amount){
        var billing = getById(id);
        billing.updateBilling(new InvoiceItem(description,amount));

        return repo.save(billing);
    }


}
