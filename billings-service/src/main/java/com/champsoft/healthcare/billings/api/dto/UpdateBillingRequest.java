package com.champsoft.healthcare.billings.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBillingRequest(
        @NotBlank String description,
        Double amount
) {
}
