package com.eugene.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderConfirmationDTO> kafkaTemplate;

    public void sendOrderConfirmation(OrderConfirmationDTO orderConfirmationDTO) {
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


