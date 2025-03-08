package com.eugene.order_service.dto;

import com.eugene.order_service.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderResponseDTO(
        Integer id,

        String reference,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String customerId
) {
}
