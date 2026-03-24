package com.tuan.ridehub.dto.response;

import com.tuan.ridehub.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentDtoResponse {
    private UUID id;
    private Double amount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentUrl;
    private String responseData;
    private UUID userId;
}
