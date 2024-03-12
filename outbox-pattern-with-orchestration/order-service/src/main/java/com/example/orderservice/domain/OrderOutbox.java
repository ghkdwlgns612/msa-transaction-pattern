package com.example.orderservice.domain;

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
@Table(name = "order_outbox")
@AllArgsConstructor
@NoArgsConstructor
public class OrderOutbox {
    @Id
    @Column(name = "order_id")
    private long orderId;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
