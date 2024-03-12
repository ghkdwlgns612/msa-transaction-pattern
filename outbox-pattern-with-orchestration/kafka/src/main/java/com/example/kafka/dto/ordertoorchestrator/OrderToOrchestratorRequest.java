package com.example.kafka.dto.ordertoorchestrator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderToOrchestratorRequest {
    private long orderId;
    private String username;
    private long price;
    private String itemName;
    private long quantity;
}
