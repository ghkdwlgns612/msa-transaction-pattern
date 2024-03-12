package com.example.stockservice.stock;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock")
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name")
    private String itemName;

    private long stock;

    public Stock(String itemName, long stock) {
        this.itemName = itemName;
        this.stock = stock;
    }

    public void adjustStock(long quantity) {
        if (stock < quantity) {
            throw new IllegalArgumentException();
        }
        this.stock -= quantity;
    }
}
