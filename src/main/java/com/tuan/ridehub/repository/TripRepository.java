package com.tuan.ridehub.repository;

import com.tuan.ridehub.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Station, UUID> {
}
