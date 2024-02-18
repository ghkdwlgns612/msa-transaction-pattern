package com.example.orderservice.schedule.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderToPaymentRequest {
    private final String username;
    private final long price;
}
