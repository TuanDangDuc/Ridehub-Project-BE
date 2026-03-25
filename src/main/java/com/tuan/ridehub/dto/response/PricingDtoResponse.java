package com.tuan.ridehub.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PricingDtoResponse {
    UUID id;
    Double pricePerMinutes;
}
