package com.example.paymentservice.balance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "balance_outbox")
@AllArgsConstructor
@NoArgsConstructor
public class BalanceOutbox {
    @Id
    @Column(name = "order_id")
    private long orderId;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
