package com.eugene.payment_service.kafkaproducer;


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
public class PaymentNotificationProducer {

    private final KafkaTemplate<String, PaymentNotificationRequestDTO> kafkaTemplate;

    public void sendPaymentNotification(PaymentNotificationRequestDTO paymentNotificationRequestDTO){

        log.info("Sending payment notification confirmation with body <{}>", paymentNotificationRequestDTO);

        Message<PaymentNotificationRequestDTO> message = MessageBuilder
                .withPayload(paymentNotificationRequestDTO)
                .setHeader(KafkaHeaders.TOPIC,  "payment-notification-topic")
                .build();
    }
}
