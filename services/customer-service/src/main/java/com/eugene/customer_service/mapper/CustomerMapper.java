package com.eugene.customer_service.mapper;

import com.eugene.customer_service.customer.Address;
import com.eugene.customer_service.customer.Customer;
import com.eugene.customer_service.dto.AddressDTO;
import com.eugene.customer_service.dto.CustomerDTO;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {

    /**
     * Converts CustomerDTO to Customer entity.
     * Uses builder pattern for Customer entity as it's a mutable class with many fields.
     */
    public Customer toCustomer(CustomerDTO customerDTO){

        if (customerDTO == null){
            return null;
        }

        return Customer.builder()
                .id(customerDTO.id())
                .firstName(customerDTO.firstName())
                .lastName(customerDTO.lastName())
                .email(customerDTO.email())
                // Ensures that the addressDTO field is converted to address field
                .address(toAddress(customerDTO.address()))
                .build();
    }

     /**
     * Converts Customer entity to CustomerDTO.
     * Uses canonical constructor for CustomerDTO as it's a record (immutable data carrier).
     * Records are designed for simple, transparent data transfer and don't need builders.
     */
    public CustomerDTO toCustomerDTO(Customer customer){

        if (customer == null){
            return null;
        }

        return new CustomerDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                // Ensures that the address field is converted to addressDTO field
                toAddressDTO(customer.getAddress())
        );
    }

    /**
     * Converts AddressDTO to Address entity.
     * Required because CustomerDTO uses AddressDTO (record) while Customer entity uses Address (class).
     * This conversion maintains proper separation between API layer (DTOs) and persistence layer (entities).
     */
    public Address toAddress(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        return Address.builder()
                .street(addressDTO.street())
                .houseNumber(addressDTO.houseNumber())
                .zipCode(addressDTO.zipCode())
                .build();
    }


    /**
     * Converts Address entity to AddressDTO.
     * Required to prevent exposing internal entity details to the API layer.
     * This conversion ensures clean separation of concerns between layers.
     */
    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDTO(
                address.getStreet(),
                address.getHouseNumber(),
                address.getZipCode()
        );
    }
}
