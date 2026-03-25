package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.PricingDtoRequest;
import com.tuan.ridehub.dto.response.PricingDtoResponse;
import com.tuan.ridehub.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService pricingService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePricing(
            @PathVariable UUID id
    ){
        pricingService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
