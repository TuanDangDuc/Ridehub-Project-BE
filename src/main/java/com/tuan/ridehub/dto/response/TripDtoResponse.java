package com.tuan.ridehub.dto.response;

import com.tuan.ridehub.enums.TripStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TripDtoResponse {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Double distance;
    private Double totalCost;
    private TripStatus tripStatus;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private UUID userId;
    private UUID vehicleId;
    private UUID startStationId;
    private UUID endStationId;
    private UUID pricingId;
}
