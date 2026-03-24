package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.TripDtoRequest;
import com.tuan.ridehub.dto.response.TripDtoResponse;
import com.tuan.ridehub.enums.TripStatus;
import com.tuan.ridehub.model.*;
import com.tuan.ridehub.repository.*;
import com.tuan.ridehub.service.mapper.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final StationRepository stationRepository;
    private final PricingRepository pricingRepository;
    private final TripMapper tripMapper;

    public TripDtoResponse startTrip(TripDtoRequest request) {
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Station startStation = stationRepository.findById(request.getStartStationId())
                .orElseThrow(() -> new RuntimeException("Start station not found"));
        Station endStation = stationRepository.findById(request.getEndStationId())
                .orElseThrow(() -> new RuntimeException("End station not found"));
        Pricing pricing = pricingRepository.findById(request.getPricingId())
                .orElseThrow(() -> new RuntimeException("Pricing not found"));

        Trip trip = tripMapper.toTrip(request, user, vehicle, startStation, endStation, pricing);
        trip.setStartTime(LocalDateTime.now());
        trip.setTripStatus(TripStatus.ONGOING);

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toTripResponse(savedTrip);
    }

    public TripDtoResponse endTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getTripStatus() != TripStatus.ONGOING) {
            throw new RuntimeException("Trip is not ongoing");
        }

        trip.setEndTime(LocalDateTime.now());

        // Tính duration (phút)
        long minutes = Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
        trip.setDuration((int) minutes);

        // Tính totalCost = duration * pricePerMinutes
        if (trip.getPricing() != null && trip.getPricing().getPricePerMinutes() != null) {
            trip.setTotalCost(minutes * trip.getPricing().getPricePerMinutes());
        }

        trip.setTripStatus(TripStatus.COMPLETED);

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toTripResponse(savedTrip);
    }

    public TripDtoResponse cancelTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getTripStatus() != TripStatus.ONGOING) {
            throw new RuntimeException("Only ongoing trips can be cancelled");
        }

        trip.setTripStatus(TripStatus.CANCELLED);
        trip.setEndTime(LocalDateTime.now());

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toTripResponse(savedTrip);
    }

    public TripDtoResponse getTripById(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return tripMapper.toTripResponse(trip);
    }

    public List<TripDtoResponse> getTripsByUserId(UUID userId) {
        List<Trip> trips = tripRepository.findByUser_Id(userId);
        return trips.stream()
                .map(tripMapper::toTripResponse)
                .toList();
    }

    public List<TripDtoResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toTripResponse)
                .toList();
    }
}
