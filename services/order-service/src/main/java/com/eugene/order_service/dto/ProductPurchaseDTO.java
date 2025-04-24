package com.eugene.order_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


// Using this record to pass a list of productPurchaseDTOs to the product-service's purchaseProducts() method and return a list of purchasedProducts.
public record ProductPurchaseDTO(

        @NotNull(message="Product is mandatory")
        Integer productId,

        @Positive(message="Quantity should be precised")
        Double quantity
) {
}
