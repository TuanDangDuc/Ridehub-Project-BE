package com.tuan.ridehub.repository;

import com.tuan.ridehub.model.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, UUID> {

    @Query("select a from Pricing a")
    List<Pricing> getAll();

    @Query("select a from Pricing a where a.id = ?1")
    Pricing getPricingById(UUID id);

    @Modifying
    @Transactional
    @Query("update Pricing a set a.pricePerMinutes = ?2 where a.id = ?1")
    void updatePricing(UUID id, Double pricePerMinutes);
}
