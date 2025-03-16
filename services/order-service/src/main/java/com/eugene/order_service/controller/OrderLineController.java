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
@RequestMapping("/api/v1/order-lines")
public class OrderLineController {

    private final OrderLineService orderLineService;


    // Return all orderLines for a specific order by proving the orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderLineResponseDTO>> findByOrderId(@PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(orderLineService.findAllByOrderId(orderId));
    }


}
