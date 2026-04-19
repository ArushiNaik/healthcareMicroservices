package com.champsoft.healthcare.billings.api.dto;

import com.champsoft.healthcare.billings.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBillingRequest(

        @NotBlank String description,
        @NotNull Double amount,
        @NotNull LocalDate dueDate,
        @NotNull PaymentMethod method
) {}