package com.example.orderservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "`order`")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "item_name")
    private String itemName;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(value = EnumType.STRING)
    private StockStatus stockStatus;

    private long price;

    private long quantity;

    private Order(
            String userName,
            String itemName,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            StockStatus stockStatus,
            long price,
            long quantity
    ) {
        this.userName = userName;
        this.itemName = itemName;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.stockStatus = stockStatus;
        this.price = price;
        this.quantity = quantity;
    }

    public static Order createOrder(String userName, String itemName, long price, long quantity) {
        return new Order(
                userName,
                itemName,
                OrderStatus.IN_PROGRESS,
                PaymentStatus.READY,
                StockStatus.READY,
                price,
                quantity);
    }

    public void completed() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public boolean isOrdering() {
        return this.orderStatus == OrderStatus.IN_PROGRESS;
    }

    public void linkOtherServices() {
        this.paymentStatus = PaymentStatus.IN_PROGRESS;
        this.stockStatus = StockStatus.IN_PROGRESS;
    }

    public void completePayment() {
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void completeStock() {
        this.stockStatus = StockStatus.COMPLETED;
    }

    public boolean isCompleteLinkedServices() {
        return this.paymentStatus == PaymentStatus.COMPLETED && this.stockStatus == StockStatus.COMPLETED;
    }

    public void failOrderByPayment() {
        this.orderStatus = OrderStatus.FAILED;
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void failOrderByStock() {
        this.orderStatus = OrderStatus.FAILED;
        this.stockStatus = StockStatus.FAILED;
    }

    public boolean isStockSucceeded() {
        return this.stockStatus == StockStatus.COMPLETED;
    }

    public boolean isPaymentSucceeded() {
        return this.paymentStatus == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.orderStatus == OrderStatus.FAILED;
    }
}
