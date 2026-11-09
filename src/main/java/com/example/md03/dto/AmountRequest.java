package com.example.md03.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AmountRequest {
    @NotNull(message = "amount không được để trống")
    @Min(value = 1, message = "amount phải lớn hơn 0")
    private Integer amount;
}