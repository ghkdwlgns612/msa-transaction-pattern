package com.example.paymentservice.payment;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@NoArgsConstructor
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String userName;

    private long balance;

    public Balance(String userName, long balance) {
        this.userName = userName;
        this.balance = balance;
    }
}
