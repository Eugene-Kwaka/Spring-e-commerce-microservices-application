package com.eugene.payment_service.dto;

import java.math.BigDecimal;

import com.eugene.payment_service.entity.Customer;
import com.eugene.payment_service.entity.PaymentMethod;

public record PaymentRequestDTO(

        Integer id,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        Integer orderId,

        String orderReference,

        Customer customer
) {
}
