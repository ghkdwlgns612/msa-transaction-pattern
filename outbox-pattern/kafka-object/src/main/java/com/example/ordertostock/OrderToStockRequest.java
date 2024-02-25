package com.example.ordertostock;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderToStockRequest {
    private final String itemName;
    private final long quantity;
}
