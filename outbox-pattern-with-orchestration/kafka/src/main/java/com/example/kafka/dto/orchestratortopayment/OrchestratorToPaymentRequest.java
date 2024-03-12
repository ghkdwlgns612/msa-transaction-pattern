package com.example.kafka.dto.orchestratortopayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorToPaymentRequest {
    private long orderId;
    private String username;
    private long price;
}
