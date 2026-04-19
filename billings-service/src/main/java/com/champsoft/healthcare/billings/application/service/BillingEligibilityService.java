package com.champsoft.healthcare.billings.application.service;

import com.champsoft.healthcare.billings.application.port.out.BillingRepositoryPort;
import com.champsoft.healthcare.billings.domain.exception.InvalidStatusRefund;
import com.champsoft.healthcare.billings.domain.model.BillingId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingEligibilityService {

    private final BillingRepositoryPort repo;

    public BillingEligibilityService(BillingRepositoryPort repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligibleForRefund(String billingId){
        var v= repo.findById(BillingId.of(billingId)).orElseThrow(()-> new InvalidStatusRefund("Billing cannot be refunded"));
       return  v.isEligibleForRefund();
    }

}
