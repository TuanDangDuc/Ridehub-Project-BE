package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.response.PaymentDtoResponse;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Users;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toPayment(String paymentMethod, Double amount, Users user) {
        return Payment.builder()
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .user(user)
                .build();
    }

    public PaymentDtoResponse toPaymentResponse(Payment payment) {
        return PaymentDtoResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentUrl(payment.getPaymentUrl())
                .responseData(payment.getResponseData())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .build();
    }
}
