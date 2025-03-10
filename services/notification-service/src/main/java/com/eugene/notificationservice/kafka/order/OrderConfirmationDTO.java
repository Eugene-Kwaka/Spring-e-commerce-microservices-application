package com.eugene.notificationservice.kafka.order;

import com.eugene.notificationservice.kafka.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmationDTO(

        String orderReference,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        CustomerDTO customerDTO,

        List<ProductDTO> productsDTO
) {
}
