package com.eugene.order_service.services;

import com.eugene.order_service.clients.CustomerClient;
import com.eugene.order_service.clients.ProductClient;
import com.eugene.order_service.dto.*;
import com.eugene.order_service.entity.Order;
import com.eugene.order_service.exceptions.BusinessException;
import com.eugene.order_service.kafka.OrderConfirmationDTO;
import com.eugene.order_service.kafka.OrderProducer;
import com.eugene.order_service.mapper.OrderMapper;
import com.eugene.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // -> Provides automatic Constructor injecting the final fields(dependencies) in the class.
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final OrderMapper orderMapper;

    private final OrderLineService orderLineService;

    private final OrderProducer orderProducer;


    public Integer createOrder(OrderDTO orderDTO){

        /**
         * Check for the customer from the customer-service using OpenFeign.
         * This will make a direct call to the customer-service and try to get a customer by their Id.
         * */
        CustomerDTO customer = customerClient.getCustomerById(orderDTO.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order: Customer not found"));

        /**
         * Purchase the products by calling the purchaseProducts() endpoint from product-service using OpenFeign.
         * Return a list of the purchasedProductsResponse
         */
        List<ProductPurchaseResponseDTO> purchasedProductsResponseDTO = productClient.purchaseProducts(orderDTO.productPurchasesDTO());

        // save the order in the DB
        Order order = orderRepository.save(orderMapper.toOrder(orderDTO));

        /**
         * Persist the orderLines.
         * Looping through each productPurchaseDTO in the list of productPurchasesDTO to save in the orderLine.
         * */
        for (ProductPurchaseDTO productPurchaseDTO: orderDTO.productPurchasesDTO()) {
            orderLineService.saveOrderLine(
                    new OrderLineDTO(
                            null,
                            order,
                            productPurchaseDTO.productId(),
                            productPurchaseDTO.quantity()
                    ));
        }

        // start payment process

        // Send the order confirmation to the notification-service with Kafka
        orderProducer.sendOrderConfirmation(
                new OrderConfirmationDTO(
                        orderDTO.reference(),
                        orderDTO.amount(),
                        orderDTO.paymentMethod(),
                        customer,
                        purchasedProductsResponseDTO
                )
        );

        return order.getId();
    }

    public List<OrderResponseDTO> findAllOrders(){

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();

    }

    public OrderResponseDTO findOrderById(Integer id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No order found with the provided ID " + id));

        return orderMapper.toOrderDTO(order);
    }

}

