package com.eugene.order_service.kafka;

import com.eugene.order_service.dto.CustomerDTO;
import com.eugene.order_service.dto.ProductPurchaseResponseDTO;
import com.eugene.order_service.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class will contain all the order information that will be sent to the Kafka broker*/
public record OrderConfirmationDTO(

        String orderReference,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        // Details of the customer making the order.
        CustomerDTO customer,

        // List of all the products the customer has purchased.
        List<ProductPurchaseResponseDTO> purchasedProductsResponseDTO


) {
}
