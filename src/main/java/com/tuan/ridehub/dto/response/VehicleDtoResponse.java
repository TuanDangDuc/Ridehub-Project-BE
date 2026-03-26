package com.tuan.ridehub.dto.response;

import ch.qos.logback.core.status.Status;
import com.tuan.ridehub.enums.VehicleStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VehicleDtoResponse {
    private UUID id;
    private String name;
    private String code;
    private String type;
    private VehicleStatus status;
    private Double pricePerMinutes;
    private UUID stationId;
}
