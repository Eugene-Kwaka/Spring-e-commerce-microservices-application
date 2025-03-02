package com.eugene.customer_service.mapper;

import com.eugene.customer_service.customer.Customer;
import com.eugene.customer_service.dto.CustomerDTO;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {
    public Customer toCustomer(CustomerDTO customerDTO){
        if (customerDTO == null){
            return null;
        }
        return Customer.builder()
                .id(customerDTO.id())
                .firstName(customerDTO.firstName())
                .lastName(customerDTO.lastName())
                .email(customerDTO.email())
                .address(customerDTO.address())
                .build();
    }

    public CustomerDTO toCustomerDTO(Customer customer){
        if (customer == null){
            return null;
        }
        // I won't use the Builder() class because CustomerDTO is a record and not a normal class.
        return new CustomerDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getAddress()
        );
    }
}
