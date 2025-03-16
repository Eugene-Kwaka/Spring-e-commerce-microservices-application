package com.eugene.order_service.services;

import com.eugene.order_service.dto.OrderLineDTO;
import com.eugene.order_service.dto.OrderLineResponseDTO;
import com.eugene.order_service.exceptions.BusinessException;
import com.eugene.order_service.mapper.OrderLineMapper;
import com.eugene.order_service.repository.OrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository orderLineRepo;

    private final OrderLineMapper orderLineMapper;

    public Integer saveOrderLine(OrderLineDTO orderLineDTO) {
        var order = orderLineMapper.toOrderLine(orderLineDTO);
        return orderLineRepo.save(order).getId();
    }

    public List<OrderLineResponseDTO> findAllByOrderId(Integer orderId) {
        return orderLineRepo.findAllByOrderId(orderId)
                .stream()
                .map(orderLineMapper::toOrderLineResponseDTO)
                .toList();
    }

}