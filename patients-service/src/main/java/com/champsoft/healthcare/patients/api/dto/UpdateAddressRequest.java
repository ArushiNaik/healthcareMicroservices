package com.champsoft.healthcare.patients.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAddressRequest (
         String id,
        Integer streetNumber,
         String streetName,
        String city,
        String postalCode,
         String Country
){
}
