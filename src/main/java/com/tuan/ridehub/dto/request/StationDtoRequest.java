package com.tuan.ridehub.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationDtoRequest {
    @Column(columnDefinition = "TEXT")
    private String name;
    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;
    private Integer capacity;
    private Integer currentVehicleCount;
}
