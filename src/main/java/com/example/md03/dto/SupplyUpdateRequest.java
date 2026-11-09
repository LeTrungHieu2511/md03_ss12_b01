package com.example.md03.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SupplyUpdateRequest {
    @NotBlank(message = "Tên vật tư không được để trống")
    private String name;
    private String specification;
    private String provider;

    // Hai trường cấm: chỉ khai báo để phát hiện Client gửi lên
    private Long id;
    private Integer quantity;
}