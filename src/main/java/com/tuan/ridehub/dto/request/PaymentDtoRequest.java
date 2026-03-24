package com.tuan.ridehub.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentDtoRequest {
    private UUID tripId;
    private UUID userId;
    private String paymentMethod;
}
