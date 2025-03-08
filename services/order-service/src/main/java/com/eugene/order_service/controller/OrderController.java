package com.eugene.order_service.controller;


import com.eugene.order_service.dto.OrderDTO;
import com.eugene.order_service.dto.OrderResponseDTO;
import com.eugene.order_service.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

   @PostMapping
    public ResponseEntity<Integer> createOrder(@RequestBody @Valid OrderDTO orderDTO){
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAllOrders(){
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(orderService.findOrderById(id));
    }


}
