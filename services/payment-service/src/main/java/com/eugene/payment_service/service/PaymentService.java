package com.eugene.payment_service.service;

import com.eugene.payment_service.dto.PaymentRequestDTO;
import com.eugene.payment_service.entity.Payment;
import com.eugene.payment_service.kafkaproducer.PaymentNotificationProducer;
import com.eugene.payment_service.kafkaproducer.PaymentNotificationRequestDTO;
import com.eugene.payment_service.mapper.PaymentMapper;
import com.eugene.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentNotificationProducer paymentNotificationProducer;

    public Integer createPayment(PaymentRequestDTO paymentRequestDTO) {

        // Get the Payment object from the PaymentRequestDTO object and convert it to a Payment entity object
        Payment payment = paymentMapper.toPayment(paymentRequestDTO);


        // Send a Payment notification to the Kafka Broker that a payment has been made.
        /**
         * The flow of code from paymentRequestDTO to paymentNotificationRequestDTO in the createPayment method of PaymentService is as follows:
         *  - The createPayment method receives a PaymentRequestDTO object as a parameter.
         *  - The PaymentRequestDTO object contains various attributes such as orderReference, amount, paymentMethod, and customer.
         *  - A new PaymentNotificationRequestDTO object is created using the attributes from the PaymentRequestDTO object.
         *  - The PaymentNotificationRequestDTO object is then passed to the sendPaymentNotification method of the paymentNotificationProducer.
         * The reason for using the paymentRequestDTO attributes to fill the paymentNotificationRequestDTO attributes is to pass the necessary payment information to the Kafka producer for notification purposes.*/
        paymentNotificationProducer.sendPaymentNotification(
                new PaymentNotificationRequestDTO(
                        paymentRequestDTO.orderReference(),
                        paymentRequestDTO.amount(),
                        paymentRequestDTO.paymentMethod(),
                        paymentRequestDTO.customer().firstName(),
                        paymentRequestDTO.customer().lastName(),
                        paymentRequestDTO.customer().email()
                )
        );

        // Save the payment entity and return the object's Id.
        return paymentRepository.save(payment).getId();




    }


}
