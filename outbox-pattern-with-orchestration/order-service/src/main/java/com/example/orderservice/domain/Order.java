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

    private long price;

    private long quantity;

    private Order(
            String userName,
            String itemName,
            OrderStatus orderStatus,
            long price,
            long quantity
    ) {
        this.userName = userName;
        this.itemName = itemName;
        this.orderStatus = orderStatus;
        this.price = price;
        this.quantity = quantity;
    }

    public static Order createOrder(String userName, String itemName, long price, long quantity) {
        return new Order(
                userName,
                itemName,
                OrderStatus.IN_PROGRESS,
                price,
                quantity);
    }

    public void updateStatus(boolean succeed) {
        if (succeed) {
            this.orderStatus = OrderStatus.COMPLETED;
            return;
        }
        this.orderStatus = OrderStatus.FAILED;
    }
}
