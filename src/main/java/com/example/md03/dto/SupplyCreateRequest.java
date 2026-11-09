package com.example.md03.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SupplyCreateRequest {
    @NotBlank(message = "Tên vật tư không được để trống")
    private String name;
    private String specification;
    private String provider;
    private String unit;
    // Không có quantity: Client gửi lên cũng bị bỏ qua
}