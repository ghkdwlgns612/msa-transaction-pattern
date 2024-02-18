package com.example.orderservice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDto {
    @NotNull private final String userName;
    @NotNull private final String itemName;
    @Min(100) private final long price;
    @Min(1) private final long quantity;
}
