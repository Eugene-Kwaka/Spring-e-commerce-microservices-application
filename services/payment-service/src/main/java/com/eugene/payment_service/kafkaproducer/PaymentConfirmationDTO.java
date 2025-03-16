package com.eugene.payment_service.kafkaproducer;

import com.eugene.payment_service.entity.PaymentMethod;

import java.math.BigDecimal;

public record PaymentNotificationRequestDTO(

        String orderReference,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String customerFirstName,

        String customerLastName,

        String customerEmail
) {






}
