package com.eugene.notificationservice.entity;


import com.eugene.notificationservice.kafka.order.OrderConfirmationDTO;
import com.eugene.notificationservice.kafka.payment.PaymentConfirmationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Notification {

    @Id
    private String id;

    private NotificationType notificationType;

    private String message;

    private LocalDateTime notificationDate;

    private OrderConfirmationDTO orderConfirmationDTO;

    private PaymentConfirmationDTO paymentConfirmationDTO;
}
