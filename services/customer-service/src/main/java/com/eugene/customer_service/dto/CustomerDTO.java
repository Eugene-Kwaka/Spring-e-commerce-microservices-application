package com.eugene.customer_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


public record CustomerDTO(
        String id,

        @NotNull(message ="Customer first name is required")
        String firstName,

        @NotNull(message ="Customer last name is required")
        String lastName,

        @NotNull(message = "Email address is required")
        @Email(message = "Please provide a valid email address")
        String email,

        // @Valid annotation is used to validate the AddressDTO object when it is nested within the CustomerDTO object
        @Valid
        AddressDTO address
) {
}
