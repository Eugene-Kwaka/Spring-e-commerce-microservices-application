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

    private final KafkaTemplate<String, PaymentConfirmationDTO> kafkaTemplate;

    public void sendPaymentNotification(PaymentConfirmationDTO paymentConfirmationDTO){

        // The <{}> will include the output from the serialization of the paymentConfirmationDTO object.
        log.info("Sending payment notification confirmation with body <{}>", paymentConfirmationDTO);

        Message<PaymentConfirmationDTO> message = MessageBuilder
                .withPayload(paymentConfirmationDTO)
                .setHeader(KafkaHeaders.TOPIC,  "payment-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
