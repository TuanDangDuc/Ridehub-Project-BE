package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.AddVehicleDtoRequest;
import com.tuan.ridehub.dto.request.UpdateVehicleDtoRequest;
import com.tuan.ridehub.dto.response.VehicleDtoResponse;
import com.tuan.ridehub.enums.VehicleStatus;
import com.tuan.ridehub.model.Vehicle;
import com.tuan.ridehub.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addVehicle(
            @RequestBody AddVehicleDtoRequest request
    ) {
        vehicleService.addVehicle(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<VehicleDtoResponse> getAllVehicles() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    public VehicleDtoResponse getVehicleById(
            @PathVariable UUID id
    ) {
        return vehicleService.getVehicleById(id);
    }

    @PutMapping
    public ResponseEntity<?> updateVehicle(
            @RequestBody UpdateVehicleDtoRequest request
    ) {
        vehicleService.updateVehicle(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateVehicle(
            @PathVariable UUID id,
            @RequestParam VehicleStatus status
    ) {
        vehicleService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicleById(
            @PathVariable UUID id
    ) {
        vehicleService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/station")
    public ResponseEntity<?> updateVehicleStation(
            @PathVariable UUID id,
            @RequestParam UUID stationId
    ) {
        vehicleService.updateVehicleStation(id, stationId);
        return ResponseEntity.ok().build();
    }
}
