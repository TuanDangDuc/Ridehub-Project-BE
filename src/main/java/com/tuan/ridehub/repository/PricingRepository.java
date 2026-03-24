package com.tuan.ridehub.repository;

import com.tuan.ridehub.model.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, UUID> {
}
