package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.StationDtoRequest;
import com.tuan.ridehub.dto.request.UpdateStationDtoRequest;
import com.tuan.ridehub.dto.response.StationDtoResponse;
import com.tuan.ridehub.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station")
public class StationController {
    private final StationService stationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addStation(
            @RequestBody StationDtoRequest request
    ) {
        stationService.addStation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<StationDtoResponse> getAllStation() {
        return stationService.getAllStation();
    }

    @GetMapping("/{id}")
    public StationDtoResponse getStationById(
            @PathVariable UUID id
    ) {
        return stationService.getStationById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    public ResponseEntity<?> updateStation(
            @RequestBody UpdateStationDtoRequest request
    ) {
        stationService.updateStation(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStation(
            @PathVariable UUID id
    ) {
        stationService.updateCurrentVehicleCount(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(
            @PathVariable UUID id
    ) {
        stationService.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
