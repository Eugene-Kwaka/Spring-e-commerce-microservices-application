package com.eugene.customer_service.service;

import com.eugene.customer_service.customer.Customer;
import com.eugene.customer_service.exceptions.CustomerNotFoundException;
import com.eugene.customer_service.mapper.CustomerMapper;
import com.eugene.customer_service.repository.CustomerRepository;
import com.eugene.customer_service.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// import static java.lang.String.format;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;


    public CustomerDTO createCustomer(CustomerDTO customerDTO){
        // Take the customerDTO object provided and change it to a Customer Document(Entity class) using the customerMapper's toCustomer() method
        Customer customer = customerMapper.toCustomer(customerDTO);

        // Save the newCustomer in the database using the customerRepository.save() method
        Customer newCustomer = customerRepository.save(customer);
 
        // Return the newCustomer's id once saved
        // return newCustomer.getId();

        // Convert the newCustomer entity saved in the DB to DTO and return it.
        return customerMapper.toCustomerDTO(newCustomer);
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO){
        // Looking for an existing customer using the findById() method and if not found, return the CustomerNotFoundException
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cannot update customer as no customer found with the provided ID"));

        if (customerDTO.firstName() != null){
            customer.setFirstName(customerDTO.firstName());
        }

        if (customerDTO.lastName() != null) {
            customer.setLastName(customerDTO.lastName());
        }

        if (customerDTO.email() != null) {
            customer.setEmail(customerDTO.email());
        }

        if (customerDTO.address() != null) {
            customer.setAddress(customerDTO.address());
        }

        // Save the updatedCustomer in the DB
        Customer updatedCustomer = customerRepository.save(customer);

        // Convert the updatedCustomer to a DTO and return it
        return customerMapper.toCustomerDTO(updatedCustomer);

    }

    public List<CustomerDTO> findAllCustomers(){
        return customerRepository.findAll()
                .stream()
                .map(customer -> customerMapper.toCustomerDTO(customer))
                .toList();
    }

    public CustomerDTO getCustomerById(String id){
        return customerRepository.findById(id)
                .map(customer -> customerMapper.toCustomerDTO(customer))
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided ID"));
    }


    public void deleteCustomer(String id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided ID"));

        customerRepository.deleteById(customer.getId());
    }
}
