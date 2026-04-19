package com.champsoft.healthcare.billings.api.mapper;

import com.champsoft.healthcare.billings.api.dto.BillingResponse;
import com.champsoft.healthcare.billings.domain.model.Billing;

public class BillingApiMapper {

    public static BillingResponse toResponse(Billing b){
        return new BillingResponse(
                b.idValue(), // FORCE STRING (NOT record)
                b.dueDate().dueDate(),
                b.paymentMethod(),
                b.status(),
                b.invoice().description(),
                b.invoice().getAmountItem()
        );
    }
}
