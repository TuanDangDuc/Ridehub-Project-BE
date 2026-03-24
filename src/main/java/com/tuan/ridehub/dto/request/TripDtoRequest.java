package com.tuan.ridehub.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TripDtoRequest {
    private UUID userId;
    private UUID vehicleId;
    private UUID startStationId;
    private UUID endStationId;
    private UUID pricingId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
