package com.eugene.product_service.dto;

import java.math.BigDecimal;

/**
 * Used for returning product details to the client. 
 * Typically used in endpoints like "Get Product by ID" or "List All Products."
 * */
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
