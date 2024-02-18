package com.example.orderservice.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_outbox")
@AllArgsConstructor
@NoArgsConstructor
public class OrderOutbox {
    @Id
    @Column(name = "order_id")
    private long orderId;
}
