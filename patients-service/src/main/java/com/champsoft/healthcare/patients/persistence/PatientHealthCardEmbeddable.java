package com.champsoft.healthcare.patients.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class PatientHealthCardEmbeddable {

    @Column(name="insurance_card_number", nullable=false)
    public String insuranceCard;

    @Column(name="expiration_date", nullable=false)
    public LocalDate expiryDate;

    protected PatientHealthCardEmbeddable(){

    }

    public PatientHealthCardEmbeddable(String insuranceCard, LocalDate expiryDate) {
        this.insuranceCard = insuranceCard;
        this.expiryDate = expiryDate;
    }
}
