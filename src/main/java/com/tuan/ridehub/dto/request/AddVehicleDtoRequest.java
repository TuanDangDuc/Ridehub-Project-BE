package com.tuan.ridehub.dto.request;

import com.tuan.ridehub.enums.Type;
import com.tuan.ridehub.enums.VehicleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AddVehicleDtoRequest {
    @Column(columnDefinition = "TEXT")
    private String name;
    private String code;
    private Type type;
    private UUID pricingId;
    private UUID stationId;
}
