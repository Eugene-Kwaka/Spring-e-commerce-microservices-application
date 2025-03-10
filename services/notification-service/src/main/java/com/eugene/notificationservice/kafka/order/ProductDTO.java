package com.eugene.notificationservice.kafka.order;

import java.math.BigDecimal;

public record ProductDTO(

        Integer productId,

        String name,

        String description,

        BigDecimal price,

        Double quantity
) {
}
