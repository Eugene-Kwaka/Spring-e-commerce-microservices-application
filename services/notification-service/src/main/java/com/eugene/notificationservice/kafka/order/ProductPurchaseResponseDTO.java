package com.eugene.notificationservice.kafka.order;

import java.math.BigDecimal;

// Renamed from ProductDTO to ProductPurchaseResponseDTO to match order-service
public record ProductPurchaseResponseDTO(
        Integer productId,

        String name,

        String description,

        BigDecimal price,

        Double quantity
) {
}