package com.eugene.order_service.mapper;

import com.eugene.order_service.dto.OrderLineDTO;
import com.eugene.order_service.dto.OrderLineResponseDTO;
import com.eugene.order_service.entity.OrderLine;
import com.eugene.order_service.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {

    public OrderLine toOrderLine(OrderLineDTO orderLineDTO){
        if(orderLineDTO == null){
            return null;
        }

        return OrderLine.builder()
                .id(orderLineDTO.id())
                // We are getting the order itself that has been saved in the DB after the products have been purchased.
                .order(
                        Order.builder()
                                .id(orderLineDTO.id())
                                .build()
                )
                .productId(orderLineDTO.productId())
                .quantity(orderLineDTO.quantity())
                .build();
    }

    public OrderLineResponseDTO toOrderLineResponseDTO(OrderLine orderLine) {
        if(orderLine == null){
            return null;
        }

        return new OrderLineResponseDTO(
                orderLine.getId(),
                orderLine.getQuantity()
        );
    }
}
