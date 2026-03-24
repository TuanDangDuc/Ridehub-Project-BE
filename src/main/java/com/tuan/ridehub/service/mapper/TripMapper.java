package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.TripDtoRequest;
import com.tuan.ridehub.dto.response.TripDtoResponse;
import com.tuan.ridehub.model.*;
import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public Trip toTrip(TripDtoRequest request, Users user, Vehicle vehicle,
                       Station startStation, Station endStation, Pricing pricing) {
        return Trip.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .vehicle(vehicle)
                .startStation(startStation)
                .endStation(endStation)
                .pricing(pricing)
                .build();
    }

    public TripDtoResponse toTripResponse(Trip trip) {
        return TripDtoResponse.builder()
                .id(trip.getId())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .duration(trip.getDuration())
                .distance(trip.getDistance())
                .totalCost(trip.getTotalCost())
                .tripStatus(trip.getTripStatus())
                .latitude(trip.getLatitude())
                .longitude(trip.getLongitude())
                .userId(trip.getUser() != null ? trip.getUser().getId() : null)
                .vehicleId(trip.getVehicle() != null ? trip.getVehicle().getId() : null)
                .startStationId(trip.getStartStation() != null ? trip.getStartStation().getId() : null)
                .endStationId(trip.getEndStation() != null ? trip.getEndStation().getId() : null)
                .pricingId(trip.getPricing() != null ? trip.getPricing().getId() : null)
                .build();
    }
}
