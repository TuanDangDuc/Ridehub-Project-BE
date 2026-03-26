package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.PricingDtoRequest;
import com.tuan.ridehub.dto.response.PricingDtoResponse;
import com.tuan.ridehub.repository.PricingRepository;
import com.tuan.ridehub.service.mapper.PricingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PricingRepository pricingRepository;
    private final PricingMapper pricingMapper;

    public void addPricing(PricingDtoRequest request) {
        pricingRepository.save(pricingMapper.PricingDtoRequestToPricing(request));
    }

    public List<PricingDtoResponse> getAll() {
        return pricingRepository.getAll()
                .stream()
                .map(pricingMapper::PricingtoPricingDtoResponse)
                .toList();
    }

    public PricingDtoResponse getPricingById(UUID id) {
        return pricingMapper.PricingtoPricingDtoResponse(pricingRepository.getPricingById(id));
    }

    public void deleteById(UUID id) {
        pricingRepository.deleteById(id);
    }

    public void updatePricing(UUID id, Double pricing) {
        pricingRepository.updatePricing(id, pricing);
    }
}
