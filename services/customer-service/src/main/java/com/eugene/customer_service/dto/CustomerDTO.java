package com.eugene.customer_service.dto;

import com.eugene.customer_service.customer.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
/**
 * This annotation from Lombok automatically generates a builder pattern implementation for the class.
 * The builder pattern provides a way to construct complex objects step by step, making the code more readable and maintainable, especially when dealing with objects that have many fields.
 * It's useful for creating instances of the `CustomerDTO` record with different combinations of fields.
 * Builder doesn't violate the record's immutability; it simply provides a more convenient way to construct the immutable object.
 * 
 */
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
