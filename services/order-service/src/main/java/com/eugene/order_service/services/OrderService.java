package com.eugene.order_service.services;

import com.eugene.order_service.clients.CustomerClient;
import com.eugene.order_service.clients.PaymentClient;
import com.eugene.order_service.clients.ProductClient;
import com.eugene.order_service.dto.*;
import com.eugene.order_service.entity.Order;
// import com.eugene.order_service.entity.OrderLine;
import com.eugene.order_service.exceptions.BusinessException;
import com.eugene.order_service.kafka.OrderConfirmationDTO;
import com.eugene.order_service.kafka.OrderProducer;
import com.eugene.order_service.mapper.OrderMapper;
import com.eugene.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // -> Provides automatic Constructor injecting the final fields(dependencies) in the class.
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final PaymentClient paymentClient;

    private final OrderMapper orderMapper;

    private final OrderLineService orderLineService;

    private final OrderProducer orderProducer;



    @Transactional
    public OrderResponseDTO createOrder(OrderDTO orderDTO){

        /**
         * Check for the customer from the customer-service using OpenFeign.
         * This will make a direct call to the customer-service and try to get a customer by their Id.
         * */
        var customer = this.customerClient.getCustomerById(orderDTO.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order: Customer not found"));

        /**
         * Purchase the products by calling the purchaseProducts() endpoint from product-service using OpenFeign.
         * Return a list of the purchasedProductsResponse
         */
        var purchasedProductsResponseDTO = productClient.purchaseProducts(orderDTO.productPurchasesDTO());

        // save the order in the DB
        Order order = this.orderRepository.save(orderMapper.toOrder(orderDTO));

        /**
         * Persist the orderLines.
         * Looping through each productPurchaseDTO in the list of productPurchasesDTO to save in the orderLine.
         * */
        for (ProductPurchaseDTO productPurchaseDTO: orderDTO.productPurchasesDTO()) {
            orderLineService.saveOrderLine(
                    new OrderLineDTO(
                            null,                           // OrderLine ID should be null for new entries
                            order.getId(),                  // Order ID reference
                            productPurchaseDTO.productId(), // Product ID
                            productPurchaseDTO.quantity()   // Quantity
                    ));
        }

        // start payment process
        /**
         * The PaymentRequestDTO constructor is taking the following arguments from orderDTO, order, and Customer objects:
         *  - orderDTO.amount() - This retrieves the amount from the OrderDTO object.
         *  - orderDTO.paymentMethod() - This retrieves the paymentMethod from the OrderDTO object.
         *  - order.getId() - This retrieves the id from the Order entity.
         *  - order.getReference() - This retrieves the reference from the Order entity.
         *  - customer - This is the Customer object retrieved from the CustomerClient.*/
        var paymentRequestDTO = new PaymentRequestDTO(
                orderDTO.amount(),
                orderDTO.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );

        paymentClient.requestOrderPayment(paymentRequestDTO);

        // Send the order confirmation to the notification-service with Kafka
        orderProducer.sendOrderConfirmation(
                new OrderConfirmationDTO(
                        orderDTO.reference(),
                        orderDTO.amount(),
                        orderDTO.paymentMethod(),
                        // From the customer object retrieved at the start of the method.
                        customer,
                        purchasedProductsResponseDTO
                )
        );

        return orderMapper.toOrderDTO(order);
    }
//     public Integer createOrder(OrderDTO orderDTO){

//         /**
//          * Check for the customer from the customer-service using OpenFeign.
//          * This will make a direct call to the customer-service and try to get a customer by their Id.
//          * */
//         var customer = this.customerClient.getCustomerById(orderDTO.customerId())
//                 .orElseThrow(() -> new BusinessException("Cannot create order: Customer not found"));

//         /**
//          * Purchase the products by calling the purchaseProducts() endpoint from product-service using OpenFeign.
//          * Return a list of the purchasedProductsResponse
//          */
//         var purchasedProductsResponseDTO = productClient.purchaseProducts(orderDTO.productPurchasesDTO());

//         // save the order in the DB
//         Order order = this.orderRepository.save(orderMapper.toOrder(orderDTO));

//         /**
//          * Persist the orderLines.
//          * Looping through each productPurchaseDTO in the list of productPurchasesDTO to save in the orderLine.
//          * */
//         for (ProductPurchaseDTO productPurchaseDTO: orderDTO.productPurchasesDTO()) {
//             orderLineService.saveOrderLine(
//                     new OrderLineDTO(
//                             null,                           // OrderLine ID should be null for new entries
//                             order.getId(),                  // Order ID reference
//                             productPurchaseDTO.productId(), // Product ID
//                             productPurchaseDTO.quantity()   // Quantity
//                     ));
//         }

//         // start payment process
//         /**
//          * The PaymentRequestDTO constructor is taking the following arguments from orderDTO, order, and Customer objects:
//          *  - orderDTO.amount() - This retrieves the amount from the OrderDTO object.
//          *  - orderDTO.paymentMethod() - This retrieves the paymentMethod from the OrderDTO object.
//          *  - order.getId() - This retrieves the id from the Order entity.
//          *  - order.getReference() - This retrieves the reference from the Order entity.
//          *  - customer - This is the Customer object retrieved from the CustomerClient.*/
//         var paymentRequestDTO = new PaymentRequestDTO(
//                 orderDTO.amount(),
//                 orderDTO.paymentMethod(),
//                 order.getId(),
//                 order.getReference(),
//                 customer
//         );

//         paymentClient.requestOrderPayment(paymentRequestDTO);

//         // Send the order confirmation to the notification-service with Kafka
//         orderProducer.sendOrderConfirmation(
//                 new OrderConfirmationDTO(
//                         orderDTO.reference(),
//                         orderDTO.amount(),
//                         orderDTO.paymentMethod(),
//                         // From the customer object retrieved at the start of the method.
//                         customer,
//                         purchasedProductsResponseDTO
//                 )
//         );

//         return order.getId();
//     }

    public List<OrderResponseDTO> findAllOrders(){

        return this.orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();

    }

    public OrderResponseDTO findOrderById(Integer id){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No order found with the provided ID " + id));

        return orderMapper.toOrderDTO(order);
    }

    public void deleteOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No order found with the provided ID " + id));

        this.orderRepository.deleteById(order.getId());
    }

}

