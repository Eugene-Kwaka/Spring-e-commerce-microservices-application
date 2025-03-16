package com.eugene.notificationservice.kafka;

import com.eugene.notificationservice.email.EmailService;
import com.eugene.notificationservice.entity.Notification;
import com.eugene.notificationservice.kafka.order.OrderConfirmationDTO;
import com.eugene.notificationservice.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.eugene.notificationservice.kafka.payment.PaymentConfirmationDTO;

import java.time.LocalDateTime;

import static com.eugene.notificationservice.entity.NotificationType.ORDER_CONFIRMATION;
import static com.eugene.notificationservice.entity.NotificationType.PAYMENT_CONFIRMATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    private final EmailService emailService;

    /**
     * This method will consume the order confirmation notification from the order-service's OrderProducer.sendOrderConfirmation() method.
     * Takes the OrderConfirmationDTO object as a parameter, which contains the:
     *  - Order reference
     *  - Total amount
     *  - Payment method
     *  - Customer details
     *  - List of purchased products
     *  Add a MessagingException to handle any errors that may occur when sending the email.
     * */
    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmationDTO orderConfirmationDTO) throws MessagingException {

        log.info("Consuming message from order-topic: {}", orderConfirmationDTO);

        notificationRepository.save(
                Notification.builder()
                        .notificationType(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmationDTO(orderConfirmationDTO)
                        .build()
        );

        // Send Email
        // Concatenate the customer's first and last name to a single customerName String variable.
        String customerName = orderConfirmationDTO.customer().firstName() + " " + orderConfirmationDTO.customer().lastName();

        emailService.sendOrderSuccessEmail(
                orderConfirmationDTO.customer().email(),
                customerName,
                orderConfirmationDTO.totalAmount(),
                orderConfirmationDTO.orderReference(),
                orderConfirmationDTO.purchasedProductsResponseDTO()
        );
    }

    /**
     * This method will consume the payment confirmation notification.
     * It listens to the messages from the payment-topic.
     * Gets triggered when the payment-service publishes a payment confirmation message.
     * I will copy the details of the payment confirmation from the payment-service/PaymentNotificationRequestDTO.
     * Add a MessagingException to handle any errors that may occur when sending the email.
     * */
    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmationDTO paymentConfirmationDTO) throws MessagingException {
        log.info("Consuming message from payment-topic: {}", paymentConfirmationDTO);

        notificationRepository.save(
                Notification.builder()
                        .notificationType(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmationDTO(paymentConfirmationDTO)
                        .build()
        );

        // Send Email
        String customerName = paymentConfirmationDTO.customerFirstName() + " " + paymentConfirmationDTO.customerLastName();

        emailService.sendPaymentSuccessEmail(
                paymentConfirmationDTO.customerEmail(),
                customerName,
                paymentConfirmationDTO.amount(),
                paymentConfirmationDTO.orderReference()
        );
    }
}
