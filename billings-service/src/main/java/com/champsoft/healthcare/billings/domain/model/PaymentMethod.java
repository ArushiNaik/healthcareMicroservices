package com.champsoft.healthcare.billings.domain.model;

import com.champsoft.healthcare.billings.domain.exception.InvalidPaymentMethodException;

public enum PaymentMethod {
    CASH,
    CREDIT_CARD,
    INSURANCE;

    public static PaymentMethod from(String value) {
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new InvalidPaymentMethodException("Invalid payment method: " + value);
        }
    }
}
