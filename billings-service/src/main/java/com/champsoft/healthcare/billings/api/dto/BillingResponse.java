package com.champsoft.healthcare.billings.api.dto;

import com.champsoft.healthcare.billings.domain.model.BillingStatus;
import com.champsoft.healthcare.billings.domain.model.PaymentMethod;

import java.time.LocalDate;

public record BillingResponse(
        String id,
        LocalDate dueDate,
        PaymentMethod paymentMethod,
        BillingStatus status,
        String description,
        Double amount
) {
}
