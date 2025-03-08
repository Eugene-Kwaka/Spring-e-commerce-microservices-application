package com.eugene.order_service.clients;

import com.eugene.order_service.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


/**
 * I need to include the customer-serviceURL property from the order-service.yml file that provides access to the customer-service.
 * I want to get a specific customer by their ID.
 * */
@FeignClient(name = "customer-service", url="${application.config.customer-serviceURL}")
public interface CustomerClient {

    @GetMapping("/{id}")
    Optional<CustomerDTO> getCustomerById(@PathVariable("id") String id);

}
