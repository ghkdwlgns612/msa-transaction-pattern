package com.example.dto.ordertopayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompensationRequest {
    private String username;
    private long price;
}
