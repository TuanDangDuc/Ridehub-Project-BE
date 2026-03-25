package com.tuan.ridehub.repository;

import com.tuan.ridehub.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {
    @Query("select a from Station a")
    List<Station> getAll();

    @Query("select a from Station a where a.id = ?1")
    Station getStationById(UUID id);

    @Modifying
    @Transactional
    @Query("update Station s set s.name = :#{#station.name}, s.latitude = :#{#station.latitude}, s.longitude = :#{#station.longitude} where s.id = :#{#station.id}")
    void updateStation(Station station);

    @Modifying
    @Transactional
    @Query("update Station s set s.currentVehicleCount = s.currentVehicleCount + 1 where s.id = ?1")
    void updateCurrentVehicleCount(UUID id);
}
