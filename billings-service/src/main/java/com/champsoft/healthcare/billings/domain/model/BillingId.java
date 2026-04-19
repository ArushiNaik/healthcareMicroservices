package com.champsoft.healthcare.billings.domain.model;

import java.util.UUID;

public class BillingId {
    private final String value;

    public BillingId(String value) {
        this.value = value;
    }

    public static BillingId newId(){
        return new BillingId(UUID.randomUUID().toString());
    }

    public static BillingId of(String value){
        return new BillingId(value);
    }

    public String value(){
        return value;
    }
}
