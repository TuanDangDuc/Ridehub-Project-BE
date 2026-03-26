package com.tuan.ridehub.service;
import com.tuan.ridehub.dto.request.AddVehicleDtoRequest;
import com.tuan.ridehub.dto.request.UpdateVehicleDtoRequest;
import com.tuan.ridehub.dto.response.VehicleDtoResponse;
import com.tuan.ridehub.enums.VehicleStatus;
import com.tuan.ridehub.model.Vehicle;
import com.tuan.ridehub.repository.VehicleRepository;
import com.tuan.ridehub.service.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;


    public void addVehicle(AddVehicleDtoRequest request) {
        vehicleRepository.save(vehicleMapper.AddVehicleDtoRequestToVehicle(request));
    }

    public List<VehicleDtoResponse> getAll() {
        return vehicleRepository.getAll()
                .stream()
                .map(vehicleMapper::VehicleToVehicleDtoResponse)
                .toList();
    }

    public VehicleDtoResponse getVehicleById(UUID id) {
        return vehicleMapper.VehicleToVehicleDtoResponse(vehicleRepository.getVehicleById(id));
    }

    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
    }

    public void updateVehicle(UpdateVehicleDtoRequest request) {
        vehicleRepository.updateVehicle(vehicleMapper.UpdateVehicleDtoRequestToVehicle(request));
    }

    public void updateStatus(UUID id, VehicleStatus status) {
        vehicleRepository.updateStatus(id, status);
    }

    public void updateVehicleStation(UUID id, UUID stationId) {
        vehicleRepository.updateVehicleStation(id, stationId);
    }
}
