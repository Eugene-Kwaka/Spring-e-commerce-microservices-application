package com.eugene.product_service.dto;

import java.math.BigDecimal;

public record ProductPurchaseResponseDTO(

        Integer productId,

        String name,

        String description,

        BigDecimal price,

        double quantity
) {
}
