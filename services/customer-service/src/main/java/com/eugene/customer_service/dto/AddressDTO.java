package com.eugene.customer_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record AddressDTO(


    @NotNull(message="Street is required")
    String street,

    @NotNull(message="House number is required")
    String houseNumber,

    // @Size annotation is used to specify the size of String values
    @Size(min=5, max=5, message="Zip code must be 5 characters long")
    String zipCode
) {

}
