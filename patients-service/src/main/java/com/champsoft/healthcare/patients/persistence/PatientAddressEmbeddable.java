package com.champsoft.healthcare.patients.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PatientAddressEmbeddable {


    @Column(name="streetNumber", nullable=false)
    Integer streetNumber;
    @Column(name="streetName", nullable=false)
    String streetName;

    @Column(name="city", nullable=false)
    String city;
    @Column(name="postalCode", nullable=false)
    String postalCode;
    @Column(name="Country", nullable=false)
    String Country;

// Integer streetNumber;
// String streetName;
//  String city;
// String postalCode;
//  String Country;

   protected PatientAddressEmbeddable(){

   }

    public PatientAddressEmbeddable(Integer streetNumber, String streetName, String city, String postalCode, String country) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.postalCode = postalCode;
        Country = country;
    }
}
