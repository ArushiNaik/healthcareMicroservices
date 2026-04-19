package com.champsoft.healthcare.patients.domain.model;

import com.champsoft.healthcare.patients.domain.exception.InvalidAddressException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Address {
    private  Integer streetNumber;
    private  String streetName;
    private  String city;
    private  String postalCode;
    private  String Country;

    public Address(Integer streetNumber, String streetName, String city, String postalCode, String country) {
        if(streetNumber == null || streetName.isEmpty() || city.isEmpty() || postalCode.isEmpty() || country.isEmpty())
            throw new InvalidAddressException("The address must be completed, you are missing information");
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.postalCode = postalCode;
        Country = country;
    }


}
