package com.tuan.ridehub.repository;

import com.tuan.ridehub.enums.VehicleStatus;
import com.tuan.ridehub.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    @Query("select v from Vehicle v join fetch v.pricing")
    List<Vehicle> getAll();

    @Query("select v from Vehicle v join fetch v.pricing where v.id = :id")
    Vehicle getVehicleById(UUID id);


    @Modifying
    @Transactional
    @Query("update Vehicle v set v.name = :#{#vehicle.name}, v.code = :#{#vehicle.code}, v.type = :#{#vehicle.type}, v.pricing.id = :#{#vehicle.pricing.id} where v.id = :#{#vehicle.id}")
    void updateVehicle(Vehicle vehicle);

    @Modifying
    @Transactional
    @Query("update Vehicle v set v.status = :status where v.id = :id")
    void updateStatus(UUID id, VehicleStatus status);

    @Modifying
    @Transactional
    @Query("update Vehicle v set v.station.id = :stationId where v.id = :id")
    void updateVehicleStation(UUID id, UUID stationId);
}
