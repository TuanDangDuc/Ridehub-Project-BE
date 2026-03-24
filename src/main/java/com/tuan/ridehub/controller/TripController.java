package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.TripDtoRequest;
import com.tuan.ridehub.dto.response.TripDtoResponse;
import com.tuan.ridehub.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip")
public class TripController {

    private final TripService tripService;

    @PostMapping("/start")
    public ResponseEntity<TripDtoResponse> startTrip(@RequestBody TripDtoRequest request) {
        return ResponseEntity.ok(tripService.startTrip(request));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<TripDtoResponse> endTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.endTrip(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TripDtoResponse> cancelTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.cancelTrip(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDtoResponse> getTripById(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripDtoResponse>> getTripsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(tripService.getTripsByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<TripDtoResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }
}
