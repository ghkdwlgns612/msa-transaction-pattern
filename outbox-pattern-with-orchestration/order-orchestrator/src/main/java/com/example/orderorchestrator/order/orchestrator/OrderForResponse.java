package com.example.orderorchestrator.order.orchestrator;

import com.example.orderorchestrator.order.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_for_response")
@AllArgsConstructor
@NoArgsConstructor
public class OrderForResponse {
    @Id
    @Column(name = "order_id")
    private long orderId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "price")
    private long price;

    @Column(name = "quantity")
    private long quantity;

    @Enumerated(EnumType.STRING)
    private Status paymentSuccess;

    @Enumerated(EnumType.STRING)
    private Status stockSuccess;

    public void failPayment() {
        this.paymentSuccess = Status.FAILED;
    }

    public void successPayment() {
        this.paymentSuccess = Status.SUCCESS;
    }

    public void failStock() {
        this.stockSuccess = Status.FAILED;
    }

    public void successStock() {
        this.stockSuccess = Status.SUCCESS;
    }

    public boolean isSucceedPayment() {
        return this.paymentSuccess == Status.SUCCESS;
    }

    public boolean isSucceedStock() {
        return this.stockSuccess == Status.SUCCESS;
    }
}
