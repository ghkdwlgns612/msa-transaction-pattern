package com.example.kafka.dto.orchestratortoorder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResultResponse {
    private long orderId;
    private boolean succeed;
}
