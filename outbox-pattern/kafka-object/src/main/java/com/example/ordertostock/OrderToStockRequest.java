package com.example.ordertostock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderToStockRequest {
    private long orderId;
    private String itemName;
    private long quantity;
}
