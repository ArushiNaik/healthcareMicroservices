package com.champsoft.healthcare.billings.application.port.out;

import com.champsoft.healthcare.billings.domain.model.Billing;
import com.champsoft.healthcare.billings.domain.model.BillingId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepositoryPort {

    Billing save(Billing bill);
    Optional<Billing> findById(BillingId id);
    //boolean existById(BillingId id);
    List<Billing> findAll();
    void deleteById(BillingId id);
}
