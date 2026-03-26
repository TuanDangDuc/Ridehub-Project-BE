package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.PricingDtoRequest;
import com.tuan.ridehub.dto.response.PricingDtoResponse;
import com.tuan.ridehub.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService pricingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> addPricing(
            @RequestBody PricingDtoRequest request
    ){
        pricingService.addPricing(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public List<PricingDtoResponse> getPricing() {
        return pricingService.getAll();
    }

    @GetMapping("/{id}")
    public PricingDtoResponse getPricing(
            @PathVariable UUID id
    ) {
        return pricingService.getPricingById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/pricePerMinutes")
    public ResponseEntity<?> updatePricing(
            @PathVariable UUID id,
            @RequestParam Double pricePerMinutes
    ) {
        pricingService.updatePricing(id, pricePerMinutes);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePricing(
            @PathVariable UUID id
    ){
        pricingService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
