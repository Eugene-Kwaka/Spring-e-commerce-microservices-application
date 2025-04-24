package com.eugene.payment_service.mapper;

import com.eugene.payment_service.dto.PaymentRequestDTO;
import com.eugene.payment_service.dto.PaymentResponseDTO;
import com.eugene.payment_service.entity.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

    public Payment toPayment(PaymentRequestDTO paymentRequestDTO) {
        if(paymentRequestDTO == null){
            return null;
        }

        return Payment.builder()
                .id(paymentRequestDTO.id())
                .amount(paymentRequestDTO.amount())
                .paymentMethod(paymentRequestDTO.paymentMethod())
                .orderId(paymentRequestDTO.orderId())
                .build();
    }

    public PaymentResponseDTO toPaymentResponseDTO(Payment payment) {

        if (payment == null) {
            return null;
        }

        return new PaymentResponseDTO(
            payment.getId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getOrderId()
        );
    }

}
