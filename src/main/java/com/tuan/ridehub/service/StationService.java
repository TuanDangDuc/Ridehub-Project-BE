package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.StationDtoRequest;
import com.tuan.ridehub.dto.request.UpdateStationDtoRequest;
import com.tuan.ridehub.dto.response.StationDtoResponse;
import com.tuan.ridehub.model.Station;
import com.tuan.ridehub.repository.StationRepository;
import com.tuan.ridehub.service.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public void addStation(StationDtoRequest request) {
        stationRepository.save(stationMapper.StationDtoRequestToStation(request));
    }

    public List<StationDtoResponse> getAllStation() {
        return stationRepository.getAll()
                .stream()
                .map(stationMapper::toStationDtoResponse)
                .toList();
    }

    public void deleteById(UUID id) {
        stationRepository.deleteById(id);
    }

    public StationDtoResponse getStationById(UUID id) {
        return stationMapper.toStationDtoResponse(stationRepository.getStationById(id));
    }

    public void updateStation(UpdateStationDtoRequest request) {
        stationRepository.updateStation(stationMapper.UpdateStationDtoRequestToStation(request));
    }

    public void updateCurrentVehicleCount(UUID id) {
        stationRepository.updateCurrentVehicleCount(id);
    }
}
