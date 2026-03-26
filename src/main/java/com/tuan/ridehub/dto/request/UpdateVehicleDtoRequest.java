package com.tuan.ridehub.dto.request;

import com.tuan.ridehub.enums.Type;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
public class UpdateVehicleDtoRequest {
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String name;
    private String code;
    private Type type;
    private UUID pricingId;
}
