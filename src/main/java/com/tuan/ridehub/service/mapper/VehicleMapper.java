package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.AddVehicleDtoRequest;
import com.tuan.ridehub.dto.request.UpdateVehicleDtoRequest;
import com.tuan.ridehub.dto.response.VehicleDtoResponse;
import com.tuan.ridehub.enums.VehicleStatus;
import com.tuan.ridehub.model.Pricing;
import com.tuan.ridehub.model.Station;
import com.tuan.ridehub.model.Vehicle;
import com.tuan.ridehub.repository.PricingRepository;
import com.tuan.ridehub.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleMapper {
    private final PricingRepository pricingRepository;
    private final StationRepository stationRepository;

    public Vehicle AddVehicleDtoRequestToVehicle(
            AddVehicleDtoRequest request
    ) {
        Pricing pricing = pricingRepository.getReferenceById(request.getPricingId());
        Station station = stationRepository.getReferenceById(request.getStationId());
        return Vehicle.builder()
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType())
                .status(VehicleStatus.AVAILABLE)
                .pricing(pricing)
                .station(station)
                .build();
    }

    public VehicleDtoResponse VehicleToVehicleDtoResponse(
            Vehicle vehicle
    ) {
        return VehicleDtoResponse.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .code(vehicle.getCode())
                .type(vehicle.getType().toString())
                .status(vehicle.getStatus())
                .pricePerMinutes(vehicle.getPricing().getPricePerMinutes())
                .stationId(vehicle.getStation().getId())
                .build();
    }

    public Vehicle UpdateVehicleDtoRequestToVehicle(
            UpdateVehicleDtoRequest request
    ) {
        Pricing pricing = pricingRepository.getReferenceById(request.getPricingId());
        return Vehicle.builder()
                .id(request.getId())
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType())
                .pricing(pricing)
                .build();
    }
}
