package com.eugene.product_service.dto;

import java.math.BigDecimal;

public record  ProductResponseDTO(

        Integer id,
        String name,
        String description,
        Double availableQuantity,
        BigDecimal price,
        Integer categoryId,
        String categoryName,
        String categoryDescription
) {
}
