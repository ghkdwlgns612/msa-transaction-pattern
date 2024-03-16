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
@Table(name = "stock_outbox")
@AllArgsConstructor
@NoArgsConstructor
public class StockOutbox {
    @Id
    @Column(name = "order_id")
    private long orderId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
