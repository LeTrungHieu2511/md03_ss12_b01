package com.example.md03.dto;

import com.example.md03.entity.Supply;

public record SupplyResponse(Long id, String name, String specification,
                             String provider, String unit, Integer quantity) {
    public static SupplyResponse from(Supply s) {
        return new SupplyResponse(s.getId(), s.getName(), s.getSpecification(),
                s.getProvider(), s.getUnit(), s.getQuantity());
    }
}