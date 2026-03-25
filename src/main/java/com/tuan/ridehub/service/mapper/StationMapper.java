package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.StationDtoRequest;
import com.tuan.ridehub.dto.request.UpdateStationDtoRequest;
import com.tuan.ridehub.dto.response.StationDtoResponse;
import com.tuan.ridehub.model.Station;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {
    public Station StationDtoRequestToStation(StationDtoRequest stationDtoRequest) {
        return Station.builder()
                .name(stationDtoRequest.getName())
                .latitude(stationDtoRequest.getLatitude())
                .longitude(stationDtoRequest.getLongitude())
                .capacity(stationDtoRequest.getCapacity())
                .currentVehicleCount(stationDtoRequest.getCurrentVehicleCount())
                .build();
    }

    public StationDtoResponse toStationDtoResponse(Station station) {
        return StationDtoResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .capacity(station.getCapacity())
                .currentVehicleCount(station.getCurrentVehicleCount())
                .build();
    }

    public Station UpdateStationDtoRequestToStation(UpdateStationDtoRequest updateStationDtoRequest) {
        return Station.builder()
                .id(updateStationDtoRequest.getId())
                .name(updateStationDtoRequest.getName())
                .latitude(updateStationDtoRequest.getLatitude())
                .longitude(updateStationDtoRequest.getLongitude())
                .capacity(updateStationDtoRequest.getCapacity())
                .build();
    }
}
