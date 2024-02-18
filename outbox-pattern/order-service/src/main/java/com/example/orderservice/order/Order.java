package com.example.orderservice.order;

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

    private String status;

    private long price;

    private long quantity;

    private Order(String userName, String itemName, String status, long price, long quantity) {
        this.userName = userName;
        this.itemName = itemName;
        this.status = status;
        this.price = price;
        this.quantity = quantity;
    }

    public static Order createOrder(String userName, String itemName, long price, long quantity) {
        return new Order(userName, itemName, "IN_PROGRESS", price, quantity);
    }
}
