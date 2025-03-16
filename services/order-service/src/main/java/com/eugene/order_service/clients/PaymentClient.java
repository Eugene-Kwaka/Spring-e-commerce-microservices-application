package com.eugene.order_service.clients;

import com.eugene.order_service.dto.PaymentRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="payment-service", url="${application.config.payment-serviceURL}")
public interface PaymentClient {

    @PostMapping
    public Integer requestOrderPayment(@RequestBody PaymentRequestDTO paymentRequestDTO);
}
