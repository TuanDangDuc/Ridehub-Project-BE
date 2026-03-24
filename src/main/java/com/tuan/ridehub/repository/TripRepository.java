package com.tuan.ridehub.repository;

import com.tuan.ridehub.enums.TripStatus;
import com.tuan.ridehub.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByUser_Id(UUID userId);

    List<Trip> findByTripStatus(TripStatus tripStatus);
}
