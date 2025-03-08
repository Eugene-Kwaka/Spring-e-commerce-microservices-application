package com.eugene.order_service.dto;

import com.eugene.order_service.entity.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequestDTO(

        BigDecimal amount,

        PaymentMethod paymentMethod,

        Integer orderId,

        String orderReference,

        CustomerDTO customer
) {
}
