package com.eugene.order_service.dto;

import com.eugene.order_service.entity.Order;

public record OrderLineDTO(

        Integer id,

        Order order,

        Integer productId,

        Double quantity
) {
}
