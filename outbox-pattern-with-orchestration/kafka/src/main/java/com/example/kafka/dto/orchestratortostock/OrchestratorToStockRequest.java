package com.example.kafka.dto.orchestratortostock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorToStockRequest {
    private long orderId;
    private String itemName;
    private long quantity;
}
