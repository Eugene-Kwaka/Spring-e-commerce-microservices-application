package com.eugene.customer_service.dto;

import com.eugene.customer_service.customer.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CustomerDTO(
        String id,

        @NotNull(message ="Customer first name is required")
        String firstName,

        @NotNull(message ="Customer last name is required")
        String lastName,

        @NotNull(message = "Email address is required")
        @Email(message = "Please provide a valid email address")
        String email,

        Address address
) {
}
