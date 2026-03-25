package com.tuan.ridehub.dto.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class StationDtoResponse {
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String name;
    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;
    private Integer capacity;
    private Integer currentVehicleCount;

}
