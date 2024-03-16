package com.example.orderorchestrator.order.orchestrator;

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
@Table(name = "payment_outbox")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOutbox {
    @Id
    @Column(name = "order_id")
    private long orderId;

    @Column(name = "username")
    private String username;

    @Column(name = "price")
    private long price;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
