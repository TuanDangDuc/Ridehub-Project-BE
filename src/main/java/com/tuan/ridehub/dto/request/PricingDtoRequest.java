package com.tuan.ridehub.dto.request;

import lombok.Data;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;

import java.util.UUID;

@Data
public class PricingDtoRequest {
    Double pricePerMinutes;
}
