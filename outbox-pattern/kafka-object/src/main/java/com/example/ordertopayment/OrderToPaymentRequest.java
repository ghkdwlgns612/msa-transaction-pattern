package com.example.ordertopayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderToPaymentRequest {
    private long orderId;
    private String username;
    private long price;
}
