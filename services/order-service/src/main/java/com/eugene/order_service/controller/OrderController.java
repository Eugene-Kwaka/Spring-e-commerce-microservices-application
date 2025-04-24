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
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderDTO orderDTO){
        return ResponseEntity.ok(this.orderService.createOrder(orderDTO));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAllOrders(){
        return ResponseEntity.ok(this.orderService.findAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable("orderId") Integer id){
        return ResponseEntity.ok(this.orderService.findOrderById(id));
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Integer id){
        this.orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
