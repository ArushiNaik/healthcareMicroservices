package com.champsoft.healthcare.billings.persistence;

import com.champsoft.healthcare.billings.application.port.out.BillingRepositoryPort;
import com.champsoft.healthcare.billings.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component("billingRepositoryPort")
public class JpaBillingRepositoryAdapter implements BillingRepositoryPort {

    private final SpringDataBillingRepository repo;

    public JpaBillingRepositoryAdapter(SpringDataBillingRepository repo) {
        this.repo = repo;
    }

    @Override
    public Billing save(Billing billing) {
        BillingJpaEntity entity = BillingMapper.toEntity(billing);
        return BillingMapper.toDomain(repo.save(entity));
    }

    @Override
    public Optional<Billing> findById(BillingId id) {
        return repo.findById(id.value())
                .map(BillingMapper::toDomain);
    }

    @Override
    public List<Billing> findAll() {
        return repo.findAll()
                .stream()
                .map(BillingMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(BillingId id) {
        repo.deleteById(id.value());
    }
}