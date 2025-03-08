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
        var Order = orderLineMapper.toOrderLine(orderLineDTO);
        return orderLineRepo.save(Order).getId();
    }

    public List<OrderLineResponseDTO> findAllByOrderId(Integer id) {
        return orderLineRepo.findAll().stream()
                .map(orderLineMapper::toOrderLineResponseDTO)
                .toList();
    }

    public OrderLineResponseDTO findByOrderId(Integer id){
        return orderLineRepo.findById(id)
                .map(orderLineMapper::toOrderLineResponseDTO)
                .orElseThrow(() -> new BusinessException("OrderLine not found"));
    }
}