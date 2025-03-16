package com.eugene.payment_service.service;

import com.eugene.payment_service.dto.PaymentRequestDTO;
import com.eugene.payment_service.entity.Payment;
import com.eugene.payment_service.kafkaproducer.PaymentNotificationProducer;
import com.eugene.payment_service.kafkaproducer.PaymentConfirmationDTO;
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

        /**
         * Get the Payment object from the PaymentRequestDTO object and convert it to a Payment entity object then save it in the database
         * This ensures data consistency as we want the payment to be saved in the database before sending the notification.
         * if something fails during the notification process, the payment will still be saved in the database.
         * */
        Payment payment = paymentRepository.save(paymentMapper.toPayment(paymentRequestDTO));


        // Send a Payment notification to the Kafka Broker that a payment has been made.
        /**
         * The flow of code from paymentRequestDTO to paymentConfirmationDTO in the createPayment method of PaymentService is as follows:
         *  - The createPayment method receives a PaymentRequestDTO object as a parameter.
         *  - The PaymentRequestDTO object contains various attributes such as orderReference, amount, paymentMethod, and customer.
         *  - A new PaymentConfirmationDTO object is created using the attributes from the PaymentRequestDTO object.
         *  - The PaymentConfirmationDTO object is then passed to the sendPaymentNotification method of the PaymentNotificationProducer.
         * The reason for using the paymentRequestDTO attributes to fill the paymentConfirmationDTO is to ensure that the payment confirmation notification contains the same information as the original payment request*/
        paymentNotificationProducer.sendPaymentNotification(
                new PaymentConfirmationDTO(
                        paymentRequestDTO.orderReference(),
                        paymentRequestDTO.amount(),
                        paymentRequestDTO.paymentMethod(),
                        paymentRequestDTO.customer().firstName(),
                        paymentRequestDTO.customer().lastName(),
                        paymentRequestDTO.customer().email()
                )
        );


        return payment.getId();

    }

}
