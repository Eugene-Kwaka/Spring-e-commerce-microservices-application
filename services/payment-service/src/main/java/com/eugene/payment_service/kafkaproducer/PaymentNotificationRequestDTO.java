package com.eugene.payment_service.kafkaproducer;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class NotificationRequestDTO {

    public void sendOrderConfirmation(NotfificationRequestDTO notificationRequestDTO) {
        log.info("Sending order confirmation: {}", orderConfirmationDTO);

        /**
         * using MessageBuilder class to create Message objects that contains the OrderConfirmationDTO payload and a header specifying the Kafka topic to which the message should be sent.*/
        Message<OrderConfirmationDTO> message = MessageBuilder
                .withPayload(orderConfirmationDTO)
                // This sets a header for the message, specifying the Kafka topic ("order-topic") where the message should be sent.
                .setHeader(KafkaHeaders.TOPIC, "order-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
