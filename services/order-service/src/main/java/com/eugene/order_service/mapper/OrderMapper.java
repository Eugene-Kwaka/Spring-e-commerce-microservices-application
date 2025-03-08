package com.eugene.order_service.mapper;

import com.eugene.order_service.dto.OrderDTO;
import com.eugene.order_service.dto.OrderResponseDTO;
import com.eugene.order_service.dto.ProductPurchaseResponseDTO;
import com.eugene.order_service.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderMapper {

    public Order toOrder(OrderDTO orderDTO){
        if(orderDTO == null){
            return null;
        }

        return Order.builder()
                .id(orderDTO.id())
                .reference(orderDTO.reference())
                .totalAmount(orderDTO.amount())
                .paymentMethod(orderDTO.paymentMethod())
                .customerId(orderDTO.customerId())
                .build();
    }

    public OrderResponseDTO toOrderDTO(Order order ) {

        if (order == null) {
            return null;
        }
        return new OrderResponseDTO(
                order.getId(),
                order.getReference(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getCustomerId()
        );
    }
}
