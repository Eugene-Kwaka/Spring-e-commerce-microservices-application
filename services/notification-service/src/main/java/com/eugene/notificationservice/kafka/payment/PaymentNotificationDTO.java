package com.eugene.notificationservice.kafka.payment;

import java.math.BigDecimal;

public record PaymentNotificationDTO(

        String orderReference,

        Double amount,

        String paymentMethod,

        String customerFirstName,

        String customerLastName,

        String customerEmail
) {
}
