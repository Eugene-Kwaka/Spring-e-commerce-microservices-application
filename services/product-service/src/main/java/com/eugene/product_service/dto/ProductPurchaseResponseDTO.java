package com.eugene.product_service.dto;

import java.math.BigDecimal;

/**
 * Used for returning details of purchased products to the client.
 * Specifically used in the context of purchase transactions.*/
public record ProductPurchaseResponseDTO(

        Integer productId,

        String name,

        String description,

        BigDecimal price,

        // Since the ProductDTO and Product(tracks the availableQuanity in stock(DB)) entity classes do not have the quantity field, 
        // we will add this as second parameter in the Mapper class so that the client can see the quantity of purchased products.
        double quantity
) {
}
