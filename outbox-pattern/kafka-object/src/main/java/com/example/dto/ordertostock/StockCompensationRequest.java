package com.example.dto.ordertostock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockCompensationRequest {
    private String itemName;
    private long quantity;
}
