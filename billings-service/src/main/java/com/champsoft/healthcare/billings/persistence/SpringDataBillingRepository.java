package com.champsoft.healthcare.billings.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBillingRepository
        extends JpaRepository<BillingJpaEntity, String> {
}