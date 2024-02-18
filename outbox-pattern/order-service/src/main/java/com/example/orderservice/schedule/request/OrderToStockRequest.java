package com.example.orderservice.schedule.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderToStockRequest {
    private final String itemName;
    private final long quantity;
}
