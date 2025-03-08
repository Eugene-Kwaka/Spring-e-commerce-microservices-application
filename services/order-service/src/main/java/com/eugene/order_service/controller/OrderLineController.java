package com.eugene.order_service.controller;


import com.eugene.order_service.services.OrderLineService;
import com.eugene.order_service.dto.OrderLineResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orderLines")

public class OrderLineController {

    private final OrderLineService orderLineService;

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderLineResponseDTO> findByOrderId(@PathVariable Integer id) {
        return ResponseEntity.ok(orderLineService.findByOrderId(id));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<List<OrderLineResponseDTO>> findAllByOrderId(@PathVariable Integer id) {
        return ResponseEntity.ok(orderLineService.findAllByOrderId(id));
    }


}
