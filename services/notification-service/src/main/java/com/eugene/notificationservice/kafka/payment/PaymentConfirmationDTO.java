package com.eugene.notificationservice.kafka.payment;

import java.math.BigDecimal;

public record PaymentConfirmationDTO(

        String orderReference,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String customerFirstName,

        String customerLastName,

        String customerEmail
) {
}
