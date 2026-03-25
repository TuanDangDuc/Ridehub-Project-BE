package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.PricingDtoRequest;
import com.tuan.ridehub.dto.response.PricingDtoResponse;
import com.tuan.ridehub.model.Pricing;
import org.springframework.stereotype.Component;

@Component
public class PricingMapper {

    public Pricing PricingDtoRequestToPricing(
            PricingDtoRequest request
    ) {
        return Pricing.builder()
                .pricePerMinutes(request.getPricePerMinutes())
                .build();
    }

    public PricingDtoResponse PricingtoPricingDtoResponse(
            Pricing pricing
    ) {
        return PricingDtoResponse.builder()
                .id(pricing.getId())
                .pricePerMinutes(pricing.getPricePerMinutes())
                .build();
    }
}
