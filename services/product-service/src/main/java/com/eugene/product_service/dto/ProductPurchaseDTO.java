package com.eugene.product_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Used for processing product purchases.
public record ProductPurchaseDTO(

        @NotNull(message = "Product is mandatory")
        Integer productId,

        @Positive(message = "Quantity is mandatory")
        double quantity
) {
}
